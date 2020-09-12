package me.schlaubi.lavakord

import com.gitlab.kordlib.core.event.VoiceServerUpdateEvent
import com.gitlab.kordlib.core.event.VoiceStateUpdateEvent
import com.gitlab.kordlib.core.on
import lavalink.client.io.Link
import org.json.JSONObject

internal class KordVoiceInterceptor(private val lavalink: KordLavaLink) {

    init {
        lavalink.client.on(consumer = ::handleVoiceServerUpdate)
        lavalink.client.on(consumer = ::handleVoiceStateUpdate)
    }

    private suspend fun handleVoiceServerUpdate(event: VoiceServerUpdateEvent) {
        val guild = event.getGuild()

        val json = JSONObject()
            .put("token", event.token)
            .put("guild_id", guild.id.value)
            .put("endpoint", event.endpoint)

        lavalink.getLink(guild.id.value).onVoiceServerUpdate(
            json,
            guild.getMember(lavalink.client.selfId).getVoiceState().sessionId
        )
    }

    private suspend fun handleVoiceStateUpdate(event: VoiceStateUpdateEvent) {
        val channel = event.state.getChannel()
        val link = event.state.guildId?.let { lavalink.getLink(it.value) } ?: error("Missing guild id")

        // Null channel means disconnected
        if (channel == null) {
            if (link.state != Link.State.DESTROYED) {
                link.onDisconnected()
            }
        } else {
            link.setChannel(channel.id.value)
        }
    }
}
