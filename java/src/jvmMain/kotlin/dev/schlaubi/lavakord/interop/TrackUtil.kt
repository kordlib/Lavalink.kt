@file:JvmName("TrackUtil")

package dev.schlaubi.lavakord.interop

import dev.arbjerg.lavalink.protocol.v4.LoadResult
import dev.arbjerg.lavalink.protocol.v4.Track
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.rest.loadItem
import java.time.Duration
import java.util.concurrent.CompletableFuture
import kotlin.time.ExperimentalTime
import kotlin.time.toJavaDuration

/**
 * Converts [Track.length] to a [Duration] because the Kotlin duration doesn't work in Java.
 */
@OptIn(ExperimentalTime::class)
public fun getLength(track: Track): Duration = Duration.ofMillis(track.info.length)

/**
 * Converts [Track.position] to a [Duration] because the Kotlin duration doesn't work in Java.
 */
@OptIn(ExperimentalTime::class)
public fun getPosition(track: Track): Duration = Duration.ofMillis(track.info.position)

/**
 * Loads an audio item from this [Link].
 *
 * See: [Lavalink doc](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#track-loading-api)
 *
 * @param node the [Node] to retrieve the search result from.
 *
 * @see TrackResponse
 */
public fun loadItem(node: Node, query: String): CompletableFuture<LoadResult> =
    node.lavakord.supply { node.loadItem(query) }

/**
 * Loads an audio item from this [Link].
 *
 * See: [Lavalink doc](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#track-loading-api)
 *
 * @param link the [JavaLink] to retrieve the item from
 * @see TrackResponse
 */
public fun loadItem(link: JavaLink, query: String): CompletableFuture<LoadResult> =
    link.lavakord.supply { link.suspendingLink.loadItem(query) }
