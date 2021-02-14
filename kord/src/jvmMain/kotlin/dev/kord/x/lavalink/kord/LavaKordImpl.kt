package dev.kord.x.lavalink.kord

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.guild.VoiceServerUpdateEvent
import dev.kord.core.event.user.VoiceStateUpdateEvent
import dev.kord.core.on
import dev.kord.x.lavalink.LavaKordOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import dev.kord.x.lavalink.audio.DiscordVoiceServerUpdateData
import dev.kord.x.lavalink.audio.Link
import dev.kord.x.lavalink.audio.Node
import dev.kord.x.lavalink.audio.internal.AbstractLavakord
import kotlin.coroutines.CoroutineContext

internal class KordLavaKord(
    internal val kord: Kord,
    userId: Long,
    shardsTotal: Int,
    options: LavaKordOptions
) : AbstractLavakord(userId, shardsTotal, options) {

    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()

    init {
        kord.on(consumer = ::handleVoiceServerUpdate)
        kord.on(consumer = ::handleVoiceStateUpdate)
        kord.on(consumer = ::onReconnect)
    }

    private fun onReconnect(event: DisconnectEvent.ReconnectingEvent) {
        launch {
            if (options.link.autoReconnect) {
                linksMap.forEach { (guildId, link) ->
                    val lastChannel = link.lastChannelId
                    if (lastChannel != null && event.kord.getGuild(Snowflake(guildId)) != null) {
                        try {
                            link.connectAudio(lastChannel.toLong())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }


    private suspend fun handleVoiceStateUpdate(event: VoiceStateUpdateEvent) {
        val channel = event.state.getChannelOrNull()
        val link = event.state.guildId.let { linksMap[it.value] } ?: return
        require(link is KordLink)

        // Null channel means disconnected
        if (channel == null) {
            if (link.state != Link.State.DESTROYED) {
                link.onDisconnected()
            }
        } else {
            link.lastChannelId = channel.id.value
            link.state = Link.State.CONNECTED
        }
    }

    override fun buildNewLink(guildId: Long, node: Node): Link =
        KordLink(guildId, node, this)

    private suspend fun handleVoiceServerUpdate(event: VoiceServerUpdateEvent) {
        val link = linksMap[event.guildId.value] ?: return
        require(link is KordLink)
        val guild = event.getGuild()

        forwardVoiceEvent(
            link,
            guild.id.asString,
            guild.getMember(event.kord.selfId).getVoiceState().sessionId,
            DiscordVoiceServerUpdateData(event.token, event.guildId.toString(), event.endpoint)
        )
    }
}
