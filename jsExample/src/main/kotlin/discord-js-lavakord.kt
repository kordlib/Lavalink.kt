@file:Suppress("KDocMissingDocumentation")

import dev.schlaubi.lavakord.LavaKordOptions
import dev.schlaubi.lavakord.MutableLavaKordOptions
import dev.schlaubi.lavakord.audio.DiscordVoiceServerUpdateData
import dev.schlaubi.lavakord.audio.Link as ILink
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.internal.AbstractLavakord
import dev.schlaubi.lavakord.audio.internal.AbstractLink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch

fun Discord.Client.lavakord(configure: MutableLavaKordOptions.() -> Unit): LavaKord =
    LavaKord(user.id.toLong(), MutableLavaKordOptions().apply(configure).seal(), this)

class LavaKord(userId: Long, options: LavaKordOptions, internal val client: Discord.Client) :
    AbstractLavakord(
        userId,
        1, // Because of how d.js sharding works this K/JS wrapper will always have 1 shard
        options
    ), CoroutineScope by GlobalScope {

    private val sessionIds = mutableMapOf<String, String>()

    init {
        client.on("raw", ::forwardEvent)
    }

    override fun buildNewLink(guildId: Long, node: Node): Link = Link(node, guildId, this)

    private fun forwardEvent(payload: dynamic) {
        launch {
            val event = payload.d
            println(payload.t.toString())
            when (payload.t.toString()) {
                "VOICE_SERVER_UPDATE" -> {
                    @Suppress("UnsafeCastFromDynamic") // Any? means literally anything
                    println(JSON.stringify(payload))
                    val link = linksMap[event.guild_id.toString().toLong()] ?: return@launch
                    val sessionId = client.guilds.fetch(event.guild_id.toString()).await().me.voice.sessionID.toString()
                    forwardVoiceEvent(
                        link,
                        event.guild_id.toString(),
                        sessionId,
                        DiscordVoiceServerUpdateData(
                            event.token.toString(), event.guild_id.toString(), event.endpoint?.toString()
                        )
                    )
                }
                "VOICE_STATE_UPDATE" -> {
                    if (event.member.user.id.toString() != client.user.id) return@launch
                    @Suppress("UnsafeCastFromDynamic") // Any? means literally anything
                    println(JSON.stringify(payload))
                    val guildId = event.guild_id.toString()
                    val sessionId = event.session_id.toString()
                    sessionIds[guildId] = sessionId

                    val link = linksMap[event.guild_id.toString().toLong()] as Link
                    if (event.channel_id == null) {
                        if (link.state != ILink.State.DESTROYED) {
                            link.onDisconnected()
                        }
                    } else {
                        link.lastChannelId = event.channel_id.toString().toLong()
                        link.state = ILink.State.CONNECTED
                    }
                }
            }

        }
    }
}

class Link(node: Node, guildId: Long, override val lavakord: LavaKord) : AbstractLink(node, guildId) {
    override suspend fun connectAudio(voiceChannelId: Long) {
        state = ILink.State.CONNECTING
        val guild = lavakord.client.guilds.fetch(guildId.toString()).await()

        guild.shard.send(makeVoiceStateCommand(voiceChannelId), true)
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
