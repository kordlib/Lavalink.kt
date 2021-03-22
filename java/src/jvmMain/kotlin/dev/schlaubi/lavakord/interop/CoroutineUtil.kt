package dev.schlaubi.lavakord.interop

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import java.util.concurrent.CompletableFuture

internal fun <T> CoroutineScope.supply(supplier: suspend CoroutineScope.() -> T) =
    future(block = supplier)

internal fun CoroutineScope.run(supplier: suspend CoroutineScope.() -> Unit): CompletableFuture<Void> =
    supply(supplier).thenApply { null }
