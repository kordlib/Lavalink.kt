package me.schlaubi.lavakord.rest

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import lavalink.client.io.LavalinkSocket
import lavalink.client.io.Link
import me.schlaubi.lavakord.asKordLink
import me.schlaubi.lavakord.audio.KordLink

private val client = HttpClient {
    install(JsonFeature) {
    }
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
fun Link.loadItem(query: String, callback: AudioLoadResultHandler) {
    val kordLink = asKordLink()
    fun loadPlaylist(response: TrackResponse, isSearch: Boolean) {
        val playlistInfo = response.getPlaylistInfo()
        val tracks = response.tracks.mapToAudioTrack()
        callback.playlistLoaded(
            BasicAudioPlaylist(
                playlistInfo.name,
                tracks,
                tracks[playlistInfo.selectedTrack],
                isSearch
            )
        )
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
suspend fun Link.loadItem(query: String): TrackResponse = asKordLink().loadItem(query)

/**
 * Maps a [List] of [TrackResponse.Track]s to a List of [AudioTrack]s.
 *
 * @see TrackResponse.Track.toAudioTrack
 */
fun List<TrackResponse.Track>.mapToAudioTrack(): List<AudioTrack> = map(TrackResponse.Track::toAudioTrack)

private suspend fun KordLink.loadItem(query: String): TrackResponse {
    val node = this.getNode(true) ?: error("No node available")
    val url = URLBuilder(node.remoteUri.toString()).apply {
        protocol = if (protocol.isSecure()) protocol.copy(name = "https") else protocol.copy(name = "http")
        path("loadtracks")
        parameters.append("identifier", query)
    }

    return client.get(url.build()) {
        headers["Authorization"] = node.password
        println(headers)
    }
}

private val LavalinkSocket.password: String
    get() = (this.javaClass.superclass.getDeclaredField("headers").apply {
        isAccessible = true
    }
        .get(this) as Map<*, *>)["Authorization"] as? String
        ?: error("Could not get password for node")
