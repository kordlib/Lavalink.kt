package dev.kord.x.lavalink.rest

import dev.kord.x.lavalink.rest.TrackResponse.*
import dev.kord.x.lavalink.rest.TrackResponse.Error.Severity
import dev.kord.x.lavalink.rest.TrackResponse.PartialTrack.Info
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName
import dev.kord.x.lavalink.audio.player.Track as NewTrack

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
    val tracks: List<PartialTrack>,
    @get:JvmName("getPlaylistInfoOrNull")
    val exception: Error? = null
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
     * @property severity the [Severity] of the error.
     *
     * @see LoadType.LOAD_FAILED
     */
    @Serializable
    public data class Error(
        val message: String,
        val severity: Severity
    ) {


        /**
         * Severity levels for FriendlyException
         *
         * Credit: https://github.com/sedmelluq/lavaplayer/blob/master/main/src/main/java/com/sedmelluq/discord/lavaplayer/tools/FriendlyException.java
         */
        @Serializable
        @Suppress("unused")
        public enum class Severity {
            /**
             * The cause is known and expected, indicates that there is nothing wrong with the library itself.
             */
            COMMON,

            /**
             * The cause might not be exactly known, but is possibly caused by outside factors. For example when an outside
             * service responds in a format that we do not expect.
             */
            SUSPICIOUS,

            /**
             * If the probable cause is an issue with the library or when there is no way to tell what the cause might be.
             * This is the default level and other levels are used in cases where the thrower has more in-depth knowledge
             * about the error.
             */
            FAULT
        }
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
    public data class PartialTrack(
        val track: String,
        val info: Info,
    ) {

        /**
         * Converts this track to an [NewTrack].
         */
        public suspend fun toTrack(): NewTrack = NewTrack.fromLavalink(track)

        /**
         * The track information.
         *
         * @property identifier the identifier created by the tracks source
         * @property isSeekable whether you can seek to a specific position or not
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
