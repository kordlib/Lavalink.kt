package dev.schlaubi.lavakord.rest.models

import dev.schlaubi.lavakord.Exception
import dev.schlaubi.lavakord.rest.models.TrackResponse.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName

/**
 * A Response from the Lavalink [Track Loading API](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#track-loading-api)
 *
 * @property loadType the type of the response
 * @property playlistInfo the [PlaylistInfo] if available otherwise an empty [NullablePlaylistInfo]
 * @property tracks a list of [Tracks](Track) found
 * @property exception the [Error] if present
 */
@Serializable
public data class TrackResponse(
    val loadType: LoadType,
    @SerialName("playlistInfo")
    @get:JvmName("getNullablePlaylistInfo")
    val playlistInfo: NullablePlaylistInfo,
    val tracks: List<PartialTrack>,
    @get:JvmName("getExceptionOrNull")
    val exception: Exception? = null
) {

    /**
     * Get's the track that was loaded when providing a link.
     * @throws IllegalStateException when the [loadType] is not [LoadType.TRACK_LOADED]
     */
    val track: PartialTrack
        get() {
            return if (loadType == LoadType.TRACK_LOADED) {
                tracks.first()
            } else {
                error("Playlist info is only available for LoadType.TRACK_LOADED")
            }
        }

    /**
     * Returns the [PlaylistInfo] if present.
     *
     * @see playlistInfo
     * @throws IllegalStateException when the [loadType] is not [LoadType.PLAYLIST_LOADED]
     */
    public fun getPlaylistInfo(): PlaylistInfo =
        if (loadType == LoadType.PLAYLIST_LOADED) playlistInfo.notNull() else error("Playlist info is only available for LoadType.PLAYLIST_LOADED")

    /**
     * Returns the [Error] if present.
     *
     * @see exception
     * @throws IllegalStateException when the [loadType] is not [LoadType.LOAD_FAILED]
     */
    public fun getException(): Exception =
        if (loadType == LoadType.LOAD_FAILED) exception!! else error("Exception is only available for LoadType.LOAD_FAILED")

    /**
     * The type of the response.
     */
    public enum class LoadType {
        /**
         * Returned when a single track is loaded.
         */
        TRACK_LOADED,

        /**
         * Returned when a playlist is loaded.
         *
         * @see PlaylistInfo
         * @see TrackResponse.playlistInfo
         */
        PLAYLIST_LOADED,

        /**
         * Returned when a search result is made (i.e ytsearch: some song).
         */
        SEARCH_RESULT,

        /**
         * Returned if no matches/sources could be found for a given identifier.
         */
        NO_MATCHES,

        /**
         * Returned if Lavaplayer failed to load something for some reason.
         *
         * @see Error
         * @see TrackResponse.exception
         */
        LOAD_FAILED
    }

    /**
     * A [PlaylistInfo] that can contain nothing.
     *
     * @property name the name of the playlist
     * @property selectedTrack the index of the selected track
     */
    @Serializable
    public data class NullablePlaylistInfo(
        val name: String? = null,
        val selectedTrack: Int? = null
    ) {
        internal fun notNull() = PlaylistInfo(name!!, selectedTrack!!)
    }

    /**
     * A [PlaylistInfo] that cannot contain nothing.
     *
     * @property name the name of the playlist
     * @property selectedTrack the index of the selected track
     */
    public data class PlaylistInfo(
        val name: String,
        val selectedTrack: Int
    )
}
