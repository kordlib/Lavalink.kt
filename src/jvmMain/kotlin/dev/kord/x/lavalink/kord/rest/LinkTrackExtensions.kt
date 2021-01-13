package dev.kord.x.lavalink.kord.rest

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist
import dev.kord.x.lavalink.audio.Link
import dev.kord.x.lavalink.rest.TrackResponse
import dev.kord.x.lavalink.rest.loadItem
import kotlinx.coroutines.launch

internal class SearchAudioPlaylist(private val tracks: List<AudioTrack>) : AudioPlaylist {
    override fun getName(): String =
        throw UnsupportedOperationException("This method is not supported on search playlists")

    override fun getTracks(): List<AudioTrack> = tracks

    override fun getSelectedTrack(): AudioTrack =
        throw UnsupportedOperationException("This method is not supported on search playlists")

    override fun isSearchResult(): Boolean = true
}

/**
 * Loads an audio item from this [Link] and calls the [callback] when the request succeeded.
 *
 * See: [Lavalink doc](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#track-loading-api)
 *
 * @see TrackResponse
 * @see com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager.loadItem
 * @see AudioLoadResultHandler
 */
@Suppress("DEPRECATION")
@Deprecated("AudioLoadResultHandler support is being discontinued as of removal of all JVM dependencies")
public fun Link.loadItem(query: String, callback: AudioLoadResultHandler) {
    fun loadPlaylist(response: TrackResponse, isSearch: Boolean) {
        val tracks = response.tracks.mapToAudioTrack()
        val playlist = if (isSearch) {
            SearchAudioPlaylist(tracks)
        } else {
            val playlistInfo = response.getPlaylistInfo()
            BasicAudioPlaylist(
                playlistInfo.name,
                tracks,
                tracks[playlistInfo.selectedTrack],
                isSearch
            )
        }

        callback.playlistLoaded(playlist)
    }

    lavakord.launch {
        val response = loadItem(query)
        when (response.loadType) {
            TrackResponse.LoadType.NO_MATCHES -> callback.noMatches()
            TrackResponse.LoadType.LOAD_FAILED -> callback.loadFailed(response.getException().toFriendlyException())
            TrackResponse.LoadType.TRACK_LOADED -> callback.trackLoaded(response.tracks.first().toAudioTrack())
            TrackResponse.LoadType.PLAYLIST_LOADED -> loadPlaylist(response, false)
            TrackResponse.LoadType.SEARCH_RESULT -> loadPlaylist(response, true)
        }
    }
}

/**
 * Maps a [List] of [TrackResponse.PartialTrack]s to a List of [AudioTrack]s.
 *
 * @see TrackResponse.PartialTrack.toAudioTrack
 */
@Suppress("DEPRECATION")
@Deprecated(
    "Please migrate to new track as of removal of all JVM dependencies",
    replaceWith = ReplaceWith("mapToTrack")
)
public fun List<TrackResponse.PartialTrack>.mapToAudioTrack(): List<AudioTrack> =
    map(TrackResponse.PartialTrack::toAudioTrack)
