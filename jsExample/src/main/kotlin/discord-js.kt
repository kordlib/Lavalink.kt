// Massive shout-out to dunste234 https://github.com/dunste123/discord.js-kotlin/blob/master/src/main/kotlin/DiscordClasses.kt
@file:Suppress("KDocMissingDocumentation")

import kotlin.js.Promise

@JsModule("discord.js")
@JsNonModule
external class Discord {

    open class Channel {
        val id: String
    }

    class Client {
        val guilds: GuildManager
        val user: ClientUser

        fun destroy(): Promise<*>
        fun login(token: String): Promise<String>
        fun on(event: String, cb: (item: dynamic) -> Unit)
    }

    class GuildManager {
        fun fetch(id: String): Promise<Guild>
    }

    class WebSocketShard {
        fun send(data: dynamic, important: Boolean)
    }

    class ClientUser : User

    class Guild : UserResolvable {
        val id: String
        val voiceStates: VoiceStateManager
        val shard: WebSocketShard
        val me: GuildMember
    }

    class VoiceStateManager {
        fun resolve(idOrInstance: Any): VoiceState?
    }

    class VoiceState {
        val channelID: String?
        val sessionID: String?
    }

    open class GuildChannel : Channel {
        val name: String
    }

    class Message : UserResolvable {
        val author: User
        val channel: TextChannel
        val content: String
        val guild: Guild
    }


    class TextChannel : GuildChannel {
        fun send(content: String): Promise<Message>
    }

    open class User : UserResolvable {
        val id: String
        val tag: String
    }

    open class GuildMember {
        val voice: VoiceState
    }

    // Things that can be resolved
    interface UserResolvable
}
