package dev.schlaubi.lavakord.kord

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.gateway.DisconnectEvent
import dev.kord.core.event.guild.VoiceServerUpdateEvent
import dev.kord.core.event.user.VoiceStateUpdateEvent
import dev.kord.core.on
import dev.schlaubi.lavakord.LavaKordOptions
import dev.schlaubi.lavakord.audio.DiscordVoiceServerUpdateData
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.internal.AbstractLavakord
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal class KordLavaKord(
    internal val kord: Kord,
    userId: ULong,
    options: LavaKordOptions
) : AbstractLavakord(userId, options) {

    override val coroutineContext: CoroutineContext = kord.coroutineContext + SupervisorJob()

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
                    if (lastChannel != null && event.kord.getGuildOrNull(Snowflake(guildId)) != null) {
                        try {
                            link.connectAudio(lastChannel)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }


    private suspend fun handleVoiceStateUpdate(event: VoiceStateUpdateEvent) {
        if (event.kord.selfId != event.state.userId) return
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

    override fun buildNewLink(guildId: ULong, node: Node): Link =
        KordLink(guildId, node, this)

    private suspend fun handleVoiceServerUpdate(event: VoiceServerUpdateEvent) {
        val link = linksMap[event.guildId.value] ?: return
        require(link is KordLink)
        val guild = event.getGuild()

        forwardVoiceEvent(
            link,
            guild.id.value,
            guild.getMember(event.kord.selfId).getVoiceState().sessionId,
            DiscordVoiceServerUpdateData(event.token, event.guildId.toString(), event.endpoint)
        )
    }
}
