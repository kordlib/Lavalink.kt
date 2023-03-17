package dev.schlaubi.lavakord.rest.models

import dev.schlaubi.lavakord.UnsafeRestApi
import dev.schlaubi.lavakord.audio.player.Filters
import kotlinx.serialization.Serializable

/**
 * Request to update a player.
 *
 * @property encodedTrack The encoded track base64 to play. null stops the current track
 * @property identifier The track identifier to play
 * @property position The track position in milliseconds
 * @property endTime The track end time in milliseconds
 * @property volume The player volume from 0 to 1000
 * @property paused Whether the player is paused
 * @property filters The new [Filters] to apply. This will override all previously applied filters
 * @property voice Information required for connecting to Discord, without connected or ping
 */
@Serializable
@UnsafeRestApi
public data class UpdatePlayerRequest(
    val encodedTrack: String? = null,
    val identifier: String? = null,
    val position: Long? = null,
    val endTime: Long? = null,
    val volume: Int? = null,
    val paused: Boolean? = null,
    val filters: Filters? = null,
    val voice: VoiceState? = null
)

/**
 * Request to update a session.
 *
 * @property resumingKey The resuming key to be able to resume this session later
 * @property timeout The timeout in seconds (default is 60s)
 */
@Serializable
@UnsafeRestApi
public data class UpdateSessionRequest(
    val resumingKey: String? = null,
    val timeout: Int? = null
)
