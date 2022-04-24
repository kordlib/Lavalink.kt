package dev.schlaubi.lavakord.audio.internal

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*

internal class ReconnectException(cause: Throwable) : RuntimeException(cause)

internal fun reconnect(cause: Throwable): Nothing = throw ReconnectException(cause)

/**
 * Connects to the ws server configured by [block] and throws a [ReconnectException] if a recoverable error occurrs.
 */
internal expect suspend fun NodeImpl.connect(
    resume: Boolean,
    block: HttpRequestBuilder.() -> Unit
): DefaultClientWebSocketSession
