package dev.schlaubi.lavakord.rest

import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.RestNode
import dev.schlaubi.lavakord.audio.player.Track
import dev.schlaubi.lavakord.rest.models.PartialTrack
import dev.schlaubi.lavakord.rest.models.TrackResponse
import dev.schlaubi.lavakord.rest.routes.V3Api
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Maps a [List] of [TrackResponse.PartialTrack]s to a List of [Track]s.
 *
 * @see TrackResponse.PartialTrack.toTrack
 */
public suspend fun List<PartialTrack>.mapToTrack(): List<Track> = map { it.toTrack() }

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
public suspend fun RestNode.loadItem(query: String): TrackResponse = get(V3Api.LoadTracks(query))

/**
 * Decodes a base64 encoded Lavalink track to a [PartialTrack].
 *
 * **Note:** This makes an API call, if you want to do this locally use [Track.fromLavalink]
 */
public suspend fun RestNode.decodeTrack(base64: String): PartialTrack = get(V3Api.DecodeTrack(base64))

/**
 * Decodes a list of base64 encoded Lavalink track to a [PartialTrack].
 *
 * **Note:** This makes an API call, if you want to do this locally use [Track.fromLavalink]
 */
public suspend fun RestNode.decodeTracks(tracks: Iterable<String>): PartialTrack = post(V3Api.DecodeTrack()) {
    setBody(tracks)
}
