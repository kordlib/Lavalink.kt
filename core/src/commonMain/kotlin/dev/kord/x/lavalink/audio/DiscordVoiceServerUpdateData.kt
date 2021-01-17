package dev.kord.x.lavalink.audio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Structure of Discord VOICE_SERVER_UPDATE event data.
 */
@Suppress("KDocMissingDocumentation")
@Serializable
public data class DiscordVoiceServerUpdateData(
    val token: String,
    @SerialName("guild_id")
    val guildId: String,
    val endpoint: String?,
)
