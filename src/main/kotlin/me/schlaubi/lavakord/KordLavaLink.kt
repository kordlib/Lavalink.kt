package me.schlaubi.lavakord

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.event.channel.VoiceChannelDeleteEvent
import com.gitlab.kordlib.core.event.gateway.DisconnectEvent
import com.gitlab.kordlib.core.event.guild.GuildDeleteEvent
import com.gitlab.kordlib.core.on
import kotlinx.coroutines.launch
import lavalink.client.io.Lavalink

internal class KordLavaLink(
    internal val client: Kord,
    private val options: KordLinkOptions,
    userId: String?,
    numShards: Int
) : Lavalink<KordLink>(userId, numShards) {

    init {
        KordVoiceInterceptor(this)

        client.on(consumer = ::onReconnect)
        client.on(consumer = ::onLeave)
        client.on(consumer = ::onChannelDeletion)
    }

    override fun buildNewLink(guildId: String?): KordLink = KordLink(this, guildId)

    private fun onReconnect(event: DisconnectEvent.ReconnectingEvent) {
        client.launch {
            if (options.autoReconnect) {
                linksMap.forEach { (guildId, link) ->
                    val lastChannel = link.lastChannel
                    if (lastChannel != null && event.kord.getGuild(Snowflake(guildId)) != null) {
                        try {
                            link.connect(lastChannel.toLong(), false)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    private fun onLeave(event: GuildDeleteEvent) {
        if (!event.unavailable) {
            linksMap[event.guildId.value]?.removeConnection()
        }
    }

    private fun onChannelDeletion(event: VoiceChannelDeleteEvent) {
        val link = linksMap[event.channel.guildId.value] ?: return
        if (event.channel.id.value == link.lastChannel) {
            link.removeConnection()
        }
    }
}
