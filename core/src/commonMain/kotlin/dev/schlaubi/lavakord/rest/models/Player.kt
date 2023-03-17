package dev.schlaubi.lavakord.rest.models

import dev.schlaubi.lavakord.audio.player.Filters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representation of a player.
 *
 * @property guildId The guild id of the player
 * @property track rack object	The current playing track
 * @property volume The volume of the player, range 0-1000, in percentage
 * @property paused Whether the player is paused
 * @property voiceState State object	The voice state of the player
 * @property filters object	The filters used by the player
 */
@Serializable
public data class Player(
    val guildId: ULong,
    val track: PartialTrack? = null,
    val volume: Int,
    val paused: Boolean,
    @SerialName("voice")
    val voiceState: VoiceState,
    val filters: Filters
)

/**
 * Representation of a Discord Voice State
 *
 * `token`, `endpoint`, and `sessionId` are the 3 required values for connecting to one of Discord's voice servers.
 * `sessionId` is provided by the Voice State Update event sent by Discord, whereas the endpoint and token are
 * provided with the Voice Server Update. Please refer to
 * [the Discord documentation](https://discord.com/developers/docs/topics/gateway-events#voice)
 *
 * @property token The Discord voice token to authenticate with
 * @property endpoint The Discord voice endpoint to connect to
 * @property sessionId The Discord voice session id to authenticate with
 * @property connected Whether the player is connected. Response only
 * @property ping Roundtrip latency in milliseconds to the voice gateway (-1 if not connected). Response only
 */
@Serializable
public data class VoiceState(
    val token: String,
    val endpoint: String?,
    val sessionId: String,
    val connected: Boolean? = null,
    val ping: Int? = null
)
