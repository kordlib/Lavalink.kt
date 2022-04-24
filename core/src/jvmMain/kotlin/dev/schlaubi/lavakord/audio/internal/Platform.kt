@file:JvmName("PlatformKtJvm")
package dev.schlaubi.lavakord.audio.internal

import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import java.net.ConnectException
import dev.schlaubi.lavakord.audio.internal.reconnect as internalReconnect

internal actual suspend fun NodeImpl.connect(
    resume: Boolean,
    block: HttpRequestBuilder.() -> Unit
): DefaultClientWebSocketSession {
    return try {
        lavakord.gatewayClient.webSocketSession(block)
    } catch (e: ServerResponseException) {
        internalReconnect(
            IllegalArgumentException(
                "The provided server responded with an invalid response code",
                e
            )
        )
    } catch (e: ConnectTimeoutException) {
        internalReconnect(IllegalStateException("The connection to the node timed out", e))
    } catch (e: ClientRequestException) {
        internalReconnect(e)
    } catch (e: ConnectException) {
        internalReconnect(e)
    }
}
