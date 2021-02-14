package dev.kord.x.lavalink.interop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import java.util.concurrent.CompletableFuture

internal fun <T> CoroutineScope.supply(supplier: suspend CoroutineScope.() -> T) =
    async(block = supplier).asCompletableFuture()

internal fun CoroutineScope.run(supplier: suspend CoroutineScope.() -> Unit): CompletableFuture<Void> =
    supply(supplier).thenApply { null }
