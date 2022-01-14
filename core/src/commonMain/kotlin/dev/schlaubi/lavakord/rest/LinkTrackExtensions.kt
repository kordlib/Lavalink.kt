package dev.schlaubi.lavakord.rest

import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.RestNode
import dev.schlaubi.lavakord.audio.player.Track
import io.ktor.util.*

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
 * @see Node.loadItem
 */
public suspend fun Link.loadItem(query: String): TrackResponse = node.loadItem(query)

/**
 * Loads an audio item from this [Link].
 *
 * See: [Lavalink doc](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#track-loading-api)
 *
 * @see TrackResponse
 */
@OptIn(InternalAPI::class) // There is no other way for parameters
public suspend fun RestNode.loadItem(query: String): TrackResponse = get {
    path("loadtracks")
    parameters.append("identifier", query)
}
