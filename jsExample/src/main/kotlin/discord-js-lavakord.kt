@file:Suppress("KDocMissingDocumentation")

import dev.schlaubi.lavakord.LavaKordOptions
import dev.schlaubi.lavakord.MutableLavaKordOptions
import dev.schlaubi.lavakord.audio.DiscordVoiceServerUpdateData
import dev.schlaubi.lavakord.audio.Link as ILink
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.internal.AbstractLavakord
import dev.schlaubi.lavakord.audio.internal.AbstractLink
import io.ktor.client.engine.js.*
import kotlinx.coroutines.*

fun Discord.Client.lavakord(configure: MutableLavaKordOptions.() -> Unit): LavaKord =
    LavaKord(user.id.toULong(), MutableLavaKordOptions().apply(configure).seal(), this)

class LavaKord(userId: ULong, options: LavaKordOptions, internal val client: Discord.Client) :
    AbstractLavakord(
        userId,
        options
    ), CoroutineScope by CoroutineScope(Dispatchers.Default) {

    private val sessionIds = mutableMapOf<String, String>()

    init {
        client.on("raw", ::forwardEvent)
    }

    override fun buildNewLink(guildId: ULong, node: Node): Link = Link(node, guildId, this)

    private fun forwardEvent(payload: dynamic) {
        launch {
            val event = payload.d
            println(payload.t.toString())
            when (payload.t.toString()) {
                "VOICE_SERVER_UPDATE" -> {
                    println(JSON.stringify(payload))
                    val link = linksMap[event.guild_id.toString().toULong()] ?: return@launch
                    val sessionId = client.guilds.fetch(event.guild_id.toString()).await().me.voice.sessionID.toString()
                    forwardVoiceEvent(
                        link,
                        event.guild_id.toString().toULong(),
                        sessionId,
                        DiscordVoiceServerUpdateData(
                            event.token.toString(), event.guild_id.toString(), event.endpoint?.toString()
                        )
                    )
                }

                "VOICE_STATE_UPDATE" -> {
                    if (event.member.user.id.toString() != client.user.id) return@launch
                    println(JSON.stringify(payload))
                    val guildId = event.guild_id.toString()
                    val sessionId = event.session_id.toString()
                    sessionIds[guildId] = sessionId

                    val link = linksMap[event.guild_id.toString().toULong()] as Link
                    if (event.channel_id == null) {
                        if (link.state != ILink.State.DESTROYED) {
                            link.onDisconnected()
                        }
                    } else {
                        link.lastChannelId = event.channel_id.toString().toULong()
                        link.state = ILink.State.CONNECTED
                    }
                }
            }

        }
    }
}

class Link(node: Node, guildId: ULong, override val lavakord: LavaKord) : AbstractLink(node, guildId) {
    override suspend fun connectAudio(voiceChannelId: ULong) {
        state = ILink.State.CONNECTING
        val guild = lavakord.client.guilds.fetch(guildId.toString()).await()

        guild.shard.send(makeVoiceStateCommand(voiceChannelId.toLong()), true)
    }

    override suspend fun disconnectAudio() {
        state = ILink.State.DISCONNECTING
        val guild = lavakord.client.guilds.fetch(guildId.toString()).await()

        guild.shard.send(makeVoiceStateCommand(null), true)
    }

    private fun makeVoiceStateCommand(voiceChannelId: Long?): dynamic {
        val payload: dynamic = object {}
        payload.op = 4
        val options: dynamic = object {}

        options.guild_id = guildId.toString()
        options.channel_id = voiceChannelId?.toString()
        options.self_mute = false
        options.self_deaf = false

        payload.d = options

        return payload
    }
}
