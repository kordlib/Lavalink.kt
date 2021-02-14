@file:JvmName("TrackUtil")

package dev.kord.x.lavalink.interop

import dev.kord.x.lavalink.audio.Link
import dev.kord.x.lavalink.audio.Node
import dev.kord.x.lavalink.audio.player.Track
import dev.kord.x.lavalink.rest.TrackResponse
import dev.kord.x.lavalink.rest.loadItem
import java.time.Duration
import java.util.concurrent.CompletableFuture
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaDuration

/**
 * Converts [Track.length] to a [Duration] because the Kotlin duration doesn't work in Java.
 */
@OptIn(ExperimentalTime::class)
public fun getLength(track: Track): Duration = track.length.toJavaDuration()

/**
 * Converts [Track.position] to a [Duration] because the Kotlin duration doesn't work in Java.
 */
@OptIn(ExperimentalTime::class)
public fun getPosition(track: Track): Duration = track.position.toJavaDuration()

/**
 * Loads an audio item from this [Link].
 *
 * See: [Lavalink doc](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#track-loading-api)
 *
 * @param node the [Node] to retrieve the search result from.
 *
 * @see TrackResponse
 */
public fun loadItem(node: Node, query: String): CompletableFuture<TrackResponse> =
    node.lavakord.supply { node.loadItem(query) }

/**
 * Loads an audio item from this [Link].
 *
 * See: [Lavalink doc](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#track-loading-api)
 *
 * @param link the [JavaLink] to retrieve the item from
 * @see TrackResponse
 */
public fun loadItem(link: JavaLink, query: String): CompletableFuture<TrackResponse> =
    link.lavakord.supply { link.suspendingLink.loadItem(query) }
