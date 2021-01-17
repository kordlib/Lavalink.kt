package dev.kord.x.lavalink.internal

import io.ktor.client.engine.*
import io.ktor.client.engine.js.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.util.date.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

public actual object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig> {
    override fun create(block: HttpClientEngineConfig.() -> Unit): HttpClientEngine =
        JsHttpClientEngine(HttpClientEngineConfig().apply(block), Js.create(block))
}

@OptIn(InternalAPI::class, KtorExperimentalAPI::class)
internal class JsHttpClientEngine(
    override val config: HttpClientEngineConfig,
    private val nonWebsocketEngine: HttpClientEngine
) : HttpClientEngineBase("lavakord-ktor-js") {

    override val dispatcher: CoroutineDispatcher get() = nonWebsocketEngine.dispatcher

    override val supportedCapabilities: Set<HttpClientEngineCapability<*>> get() = nonWebsocketEngine.supportedCapabilities

    @OptIn(KtorExperimentalAPI::class)
    @InternalAPI
    override suspend fun execute(data: HttpRequestData): HttpResponseData {
        return if (data.isUpgradeRequest() && PlatformUtils.IS_NODE) {
            executeWebsocketRequest(data)
        } else nonWebsocketEngine.execute(data)
    }

    // Adding "_capturingHack" to reduce chances of JS IR backend to rename variable,
    // so it can be accessed inside js("") function
    @Suppress("LocalVariableName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "UnsafeCastFromDynamic")
    private fun createWebsocket(urlString_capturingHack: String, headers: Headers): WebSocket {
        val ws_capturingHack = js("require('ws')")
        val headers_capturingHack: dynamic = object {}
        headers.forEach { name, values ->
            val value = values.first()
            headers_capturingHack[name] = value
        }
        return js("""new ws_capturingHack(urlString_capturingHack, { headers: headers_capturingHack })""")
    }

    private suspend fun executeWebsocketRequest(data: HttpRequestData): HttpResponseData {
        val requestTime = GMTDate()
        val callContext = callContext()
        val urlString = data.url.toString()
        val socket = createWebsocket(urlString, data.headers)
        try {
            socket.awaitConnection()
        } catch (cause: Throwable) {
            callContext.cancel(CancellationException("Failed to connect to $urlString", cause))
            throw cause
        }

        val session = JsWebSocketSession(callContext, socket)

        return HttpResponseData(
            HttpStatusCode.OK,
            requestTime,
            Headers.Empty,
            HttpProtocolVersion.HTTP_1_1,
            session,
            callContext
        )
    }

}

private suspend fun WebSocket.awaitConnection(): WebSocket = suspendCancellableCoroutine { continuation ->
    if (continuation.isCancelled) return@suspendCancellableCoroutine

    val eventListener = { event: Event ->
        when (event.type) {
            "open" -> continuation.resume(this)
            "error" -> continuation.resumeWithException(WebSocketException(JSON.stringify(event)))
        }
    }

    addEventListener("open", callback = eventListener)
    addEventListener("error", callback = eventListener)

    continuation.invokeOnCancellation {
        removeEventListener("open", callback = eventListener)
        removeEventListener("error", callback = eventListener)

        if (it != null) {
            this@awaitConnection.close()
        }
    }
}
