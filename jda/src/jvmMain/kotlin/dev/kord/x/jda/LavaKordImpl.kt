package dev.kord.x.jda

import dev.kord.x.lavalink.LavaKordOptions
import dev.kord.x.lavalink.audio.DiscordVoiceServerUpdateData
import dev.kord.x.lavalink.audio.Link
import dev.kord.x.lavalink.audio.Node
import dev.kord.x.lavalink.audio.internal.AbstractLavakord
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.ReconnectedEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor
import kotlin.coroutines.CoroutineContext

internal class JDALavakord(
    internal val jdaProvider: (Int) -> JDA,
    override val coroutineContext: CoroutineContext,
    userId: Long,
    shardsTotal: Int,
    options: LavaKordOptions
) : AbstractLavakord(userId, shardsTotal, options), VoiceDispatchInterceptor, EventListener {

    override fun buildNewLink(guildId: Long, node: Node): Link = JDALink(this, node, guildId)

    override fun onVoiceServerUpdate(update: VoiceDispatchInterceptor.VoiceServerUpdate) {
        val link = getLink(update.guildIdLong)

        launch {
            forwardVoiceEvent(
                link,
                update.guildId,
                update.sessionId,
                DiscordVoiceServerUpdateData(
                    update.token,
                    update.guildId,
                    update.endpoint
                )
            )
        }
    }

    override fun onVoiceStateUpdate(update: VoiceDispatchInterceptor.VoiceStateUpdate): Boolean {
        val channel = update.channel
        val link = getLink(update.guildIdLong)
        require(link is JDALink)

        // Null channel means disconnected
        if (channel == null) {
            if (link.state != Link.State.DESTROYED) {
                link.state = Link.State.DESTROYED
            }
        } else {
            link.lastChannelId = channel.idLong
            link.state = Link.State.CONNECTED
        }

        return link.state == Link.State.CONNECTED
    }

    @SubscribeEvent
    override fun onEvent(event: GenericEvent) {
        if (event is ReconnectedEvent) {
            launch {
                if (options.link.autoReconnect) {
                    linksMap.forEach { (_, link) ->
                        val lastChannel = link.lastChannelId
                        if (lastChannel != null && event.jda.getVoiceChannelById(lastChannel) != null) {
                            link.connectAudio(lastChannel.toLong())
                        }
                    }
                }
            }

        }
    }
}
