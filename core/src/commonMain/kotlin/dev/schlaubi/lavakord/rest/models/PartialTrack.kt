package dev.schlaubi.lavakord.rest.models

import dev.schlaubi.lavakord.audio.player.Track
import kotlinx.serialization.Serializable

/**
 * A Track.
 *
 * @property encoded the base64 encoded track
 * @property info [Info] object
 */
@Serializable
public data class PartialTrack(
    val encoded: String,
    val info: Info
) {
    /**
     * Audio Track info.
     *
     * @property identifier the identifier created by the tracks source
     * @property isSeekable whether you can seek to a specific position or not
     * @property author the author of the track
     * @property length the length of the track in ms
     * @property isStream whether the track is a stream or not
     * @property position the current position of the track in milliseconds
     * @property title the title of the track
     * @property uri the uri to the track
     * @property sourceName the name of the lavaplayer source, lavalink used to provide this track
     */
    @Serializable
    public data class Info(
        val identifier: String,
        val isSeekable: Boolean,
        val author: String,
        val length: Long,
        val isStream: Boolean,
        val position: Int,
        val title: String,
        val uri: String,
        val sourceName: String
    )

    /**
     * Converts this track to an [NewTrack].
     */
    public suspend fun toTrack(): Track = Track.fromLavalink(encoded)

}
