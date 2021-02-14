@file:JvmName("JavaInterop")

package dev.kord.x.lavalink.interop

import dev.kord.x.lavalink.LavaKord
import java.util.concurrent.CompletableFuture

/**
 * Creates a Java compatible interface for [lavakord] using [CompletableFuture].
 *
 * For Rest actions please refer to `TrackUtil` and `RoutePlannerUtil`
 */
public fun createJavaInterface(lavakord: LavaKord): JavaLavakord = JavaLavakord(lavakord)
