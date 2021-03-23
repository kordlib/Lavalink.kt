@file:JvmName("JavaInterop")

package dev.schlaubi.lavakord.interop

import java.util.concurrent.CompletableFuture

/**
 * Creates a Java compatible interface for [lavakord] using [CompletableFuture].
 *
 * For Rest actions please refer to `TrackUtil` and `RoutePlannerUtil`
 */
public fun createJavaInterface(lavakord: dev.schlaubi.lavakord.LavaKord): JavaLavakord = JavaLavakord(lavakord)
