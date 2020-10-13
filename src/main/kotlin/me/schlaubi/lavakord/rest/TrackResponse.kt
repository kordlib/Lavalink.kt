package me.schlaubi.lavakord.rest

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kotlinx.serialization.Serializable
import lavalink.client.LavalinkUtil
import me.schlaubi.lavakord.rest.TrackResponse.*

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
    @get:JvmName("getNullablePlaylistInfo")
    val playlistInfo: NullablePlaylistInfo,
    val tracks: List<Track>,
    @get:JvmName("getPlaylistInfoOrNull")
    val exception: Error? = null
) {

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
    public fun getException(): Error =
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
     * An Error reported from lavalink/lavaplayer.
     *
     * @property message the message of the error
     * @property severity the [FriendlyException.Severity] of the error.
     *
     * @see LoadType.LOAD_FAILED
     */
    @Serializable
    public data class Error(
        val message: String,
        val severity: FriendlyException.Severity
    ) {
        /**
         * Converts the error into a [FriendlyException].
         */
        public fun toFriendlyException(): FriendlyException = FriendlyException(message, severity, null)
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

    /**
     * A Track.
     *
     * @property track the base64 encoded track
     * @property info the parsed [Info]
     */
    @Serializable
    public data class Track(
        val track: String,
        val info: Info
    ) {

        /**
         * Converts this track to an [AudioTrack].
         */
        public fun toAudioTrack(): AudioTrack = LavalinkUtil.toAudioTrack(track)

        /**
         * The track information.
         *
         * @property identifier the identifier created by the tracks source
         * @property isSeekable whether you can use [AudioTrack.setPosition] I think lavadoc does not tell me
         * @property author the author of the track
         * @property length the length of the track in ms
         * @property isStream whether the track is a stream or not
         * @property position the current position of the track in milliseconds
         * @property title the title of the track
         * @property uri the uri to the track
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
            val uri: String
        )
    }
}
