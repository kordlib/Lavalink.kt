package dev.schlaubi.lavakord.plugins.lavasrc

import dev.arbjerg.lavalink.protocol.v4.Playlist
import dev.arbjerg.lavalink.protocol.v4.Track
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Provides the [ExtendedPlaylistInfo] for this [Playlist].
 */
public val Track.lavaSrcInfo: ExtendedAudioTrack
    get() = json.decodeFromJsonElement(JsonObject(pluginInfo))

/**
 * The extended audio track provided by LavaSrc.
 *
 * @property albumName The name of the album of the track
 * @property artistArtworkUrl the url to the song's artist's artowrk
 * @property previewUrl the url of a preview of the song
 * @property isPreview if this song is a preview
 */
@Serializable
public data class ExtendedAudioTrack(
    val albumName: String,
    val artistArtworkUrl: String,
    val previewUrl: String,
    val isPreview: Boolean
)
