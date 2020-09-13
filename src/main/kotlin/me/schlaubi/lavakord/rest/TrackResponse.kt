package me.schlaubi.lavakord.rest

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import kotlinx.serialization.Serializable
import lavalink.client.LavalinkUtil

@Serializable
data class TrackResponse(
    val loadType: LoadType,
    @get:JvmName("getNullablePlaylistInfo")
    val playlistInfo: NullablePlaylistInfo,
    val tracks: List<Track>,
    @get:JvmName("getPlaylistInfoOrNull")
    val exception: Error? = null
) {

    fun getPlaylistInfo(): PlaylistInfo =
        if (loadType == LoadType.PLAYLIST_LOADED) playlistInfo.notNull() else error("Playlist info is only available for LoadType.PLAYLIST_LOADED")

    fun getException(): Error =
        if (loadType == LoadType.LOAD_FAILED) exception!! else error("Exception is only available for LoadType.LOAD_FAILED")

    enum class LoadType {
        TRACK_LOADED,
        PLAYLIST_LOADED,
        SEARCH_RESULT,
        NO_MATCHES,
        LOAD_FAILED
    }

    @Serializable
    data class Error(
        val message: String,
        val severity: FriendlyException.Severity
    ) {
        fun toFriendlyException(): FriendlyException = FriendlyException(message, severity, null)
    }

    @Serializable
    data class NullablePlaylistInfo(
        val name: String? = null,
        val selectedTrack: Int? = null
    ) {
        internal fun notNull() = PlaylistInfo(name!!, selectedTrack!!)
    }

    data class PlaylistInfo(
        val name: String,
        val selectedTrack: Int
    )

    @Serializable
    data class Track(
        val track: String,
        val info: Info
    ) {
        fun toAudioTrack(): AudioTrack = LavalinkUtil.toAudioTrack(track)

        @Serializable
        data class Info(
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
