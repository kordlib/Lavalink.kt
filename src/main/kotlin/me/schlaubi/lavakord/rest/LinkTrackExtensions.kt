package me.schlaubi.lavakord.rest

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist
import kotlinx.coroutines.launch
import lavalink.client.io.Link
import me.schlaubi.lavakord.asKordLink
import me.schlaubi.lavakord.audio.KordLink

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
public fun Link.loadItem(query: String, callback: AudioLoadResultHandler) {
    val kordLink = asKordLink()
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

    kordLink.lavalink.client.launch {
        val response = kordLink.loadItem(query)
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
 * Loads an audio item from this [Link].
 *
 * See: [Lavalink doc](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#track-loading-api)
 *
 * @see TrackResponse
 * @see com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager.loadItem
 */
public suspend fun Link.loadItem(query: String): TrackResponse = asKordLink().loadItem(query)

/**
 * Maps a [List] of [TrackResponse.Track]s to a List of [AudioTrack]s.
 *
 * @see TrackResponse.Track.toAudioTrack
 */
public fun List<TrackResponse.Track>.mapToAudioTrack(): List<AudioTrack> = map(TrackResponse.Track::toAudioTrack)

private suspend fun KordLink.loadItem(query: String): TrackResponse {
    val node = this.getNode(true) ?: error("No node available")
    val url = node.buildUrl {
        path("loadtracks")
        parameters.append("identifier", query)
    }

    return node.get(url)
}
