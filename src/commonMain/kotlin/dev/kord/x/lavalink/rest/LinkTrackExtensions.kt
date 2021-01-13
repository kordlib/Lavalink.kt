package dev.kord.x.lavalink.rest

import dev.kord.x.lavalink.audio.Link
import dev.kord.x.lavalink.audio.player.Track

/**
 * Maps a [List] of [TrackResponse.PartialTrack]s to a List of [Track]s.
 *
 * @see TrackResponse.PartialTrack.toTrack
 */
public suspend fun List<TrackResponse.PartialTrack>.mapToTrack(): List<Track> = map { it.toTrack() }

/**
 * Loads an audio item from this [Link].
 *
 * See: [Lavalink doc](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#track-loading-api)
 *
 * @see TrackResponse
 */
public suspend fun Link.loadItem(query: String): TrackResponse {
    val url = node.buildUrl {
        path("loadtracks")
        parameters.append("identifier", query)
    }

    return node.get(url)
}
