package dev.schlaubi.lavakord.rest

import dev.arbjerg.lavalink.protocol.v4.LoadResult
import dev.arbjerg.lavalink.protocol.v4.Track
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.RestNode
import dev.schlaubi.lavakord.audio.player.LavaplayerTrack
import dev.schlaubi.lavakord.rest.routes.V4Api
import io.ktor.client.request.*

/**
 * Maps a [List] of [Track]s to a List of [LavaplayerTracks][LavaplayerTrack].
 *
 * @see LavaplayerTrack.fromLavalink
 */
public suspend fun List<Track>.mapToTrack(): List<LavaplayerTrack> = map { LavaplayerTrack.fromLavalink(it.encoded) }

/**
 * Loads an audio item from this [Link].
 *
 * See: [Lavalink doc](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#track-loading-api)
 *
 * @see LoadResult
 * @see Node.loadItem
 */
public suspend fun Link.loadItem(query: String): LoadResult = node.loadItem(query)

/**
 * Loads an audio item from this [Link].
 *
 * See: [Lavalink doc](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#track-loading-api)
 *
 * @see LoadResult
 */
public suspend fun RestNode.loadItem(query: String): LoadResult = get(V4Api.LoadTracks(query))

/**
 * Decodes a base64 encoded Lavalink track to a [Track].
 *
 * **Note:** This makes an API call, if you want to do this locally use [LavaplayerTrack.fromLavalink]
 */
public suspend fun RestNode.decodeTrack(base64: String): Track = get(V4Api.DecodeTrack(base64))

/**
 * Decodes a list of base64 encoded Lavalink track to a [Track].
 *
 * **Note:** This makes an API call, if you want to do this locally use [LavaplayerTrack.fromLavalink]
 */
public suspend fun RestNode.decodeTracks(tracks: Iterable<String>): List<Track> = post(V4Api.DecodeTracks()) {
    setBody(tracks.toList())
}
