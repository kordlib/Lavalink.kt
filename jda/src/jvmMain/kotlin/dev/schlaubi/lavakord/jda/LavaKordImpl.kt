package dev.schlaubi.lavakord.jda

import dev.schlaubi.lavakord.LavaKordOptions
import dev.schlaubi.lavakord.audio.DiscordVoiceServerUpdateData
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.internal.AbstractLavakord
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.session.SessionRecreateEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.SubscribeEvent
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor
import kotlin.coroutines.CoroutineContext

internal class JDALavakord(
    internal val jdaProvider: (Int) -> JDA,
    override val coroutineContext: CoroutineContext,
    userId: ULong,
    val shardsTotal: Int,
    options: LavaKordOptions
) : AbstractLavakord(userId, options), VoiceDispatchInterceptor, EventListener {

    override fun buildNewLink(guildId: ULong, node: Node): Link = JDALink(this, node, guildId)

    override fun onVoiceServerUpdate(update: VoiceDispatchInterceptor.VoiceServerUpdate) {
        val link = getLink(update.guildIdLong.toULong())

        launch {
            forwardVoiceEvent(
                link,
                update.guildIdLong.toULong(),
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
        val link = getLink(update.guildIdLong.toULong())
        require(link is JDALink)

        // Null channel means disconnected
        if (channel == null) {
            if (link.state != Link.State.DESTROYED) {
                link.state = Link.State.DESTROYED
            }
        } else {
            link.lastChannelId = channel.idLong.toULong()
            link.state = Link.State.CONNECTED
        }

        return link.state == Link.State.CONNECTED
    }

    @SubscribeEvent
    override fun onEvent(event: GenericEvent) {
        if (event is SessionRecreateEvent) {
            launch {
                if (options.link.autoReconnect) {
                    linksMap.forEach { (_, link) ->
                        val lastChannel = link.lastChannelId
                        if (lastChannel != null && event.jda.getVoiceChannelById(lastChannel.toLong()) != null) {
                            link.connectAudio(lastChannel)
                        }
                    }
                }
            }

        }
    }
}
