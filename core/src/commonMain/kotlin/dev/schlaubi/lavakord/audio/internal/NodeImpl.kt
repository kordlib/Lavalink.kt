package dev.schlaubi.lavakord.audio.internal

import dev.schlaubi.lavakord.audio.Event
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.rest.models.UpdateSessionRequest
import dev.schlaubi.lavakord.rest.routes.V3Api
import dev.schlaubi.lavakord.rest.updateSession
import io.ktor.client.plugins.*
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.serialization.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import mu.KotlinLogging
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal val LOG = KotlinLogging.logger { }

private data class SessionIdContainer(private var value: String? = null) : ReadWriteProperty<Any?, String> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String = value
        ?: error("WebSocket connection is not ready yet, please wait for the handshake to finish")

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        this.value = value
    }
}

internal class NodeImpl(
    override val host: Url,
    override val name: String,
    override val authenticationHeader: String,
    override val lavakord: AbstractLavakord,
) : Node {

    private val resumeTimeout = lavakord.options.link.resumeTimeout
    private val retry = lavakord.options.link.retry

    private val resumeKey = generateResumeKey()
    override var sessionId: String by SessionIdContainer()
    override var available: Boolean = true
    override var lastStatsEvent: GatewayPayload.StatsEvent? = null
    private var eventPublisher: MutableSharedFlow<Event> =
        MutableSharedFlow(extraBufferCapacity = Channel.UNLIMITED)
    private lateinit var session: DefaultClientWebSocketSession
    override val coroutineScope: CoroutineScope
        get() = lavakord

    override val events: SharedFlow<Event>
        get() = eventPublisher.asSharedFlow()

    internal suspend fun connect(resume: Boolean = false) {
        try {
            session = try {
                connect(resume) {
                    addUrl()
                    timeout {
                        requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
                    }
                    header("Authorization", authenticationHeader)
                    header("User-Id", lavakord.userId)
                    header("Client-Name", "Lavalink.kt")
                    if (resume) {
                        header("Resume-Key", resumeKey)
                    }
                }
            } catch (e: ReconnectException) {
                reconnect(e.cause, resume)
                return
            }

            retry.reset()
            available = true

            LOG.debug { "Successfully connected to node: $name ($host)" }

            while (!session.incoming.isClosedForReceive) {
                try {
                    onEvent(session.receiveDeserialized())
                } catch (e: WebsocketDeserializeException) {
                    LOG.warn(e) { "An error occurred whilst decoding incoming websocket packet" }
                }
            }
            val reason = session.closeReason.await()
            if (reason?.knownReason == CloseReason.Codes.NORMAL) return
            available = false
            LOG.warn { "Disconnected from websocket for: $reason. Music will continue playing if we can reconnect within the next $resumeTimeout seconds" }
            reconnect(resume = true)
        } catch (e: ClosedReceiveChannelException) {
            LOG.warn(e) { "WebSocket connection was closed abnormally" }
            reconnect(resume = true)
        }
    }

    private suspend fun reconnect(e: Throwable? = null, resume: Boolean = false) {
        LOG.error(e) { "Exception whilst trying to connect. Reconnecting" }
        if (retry.hasNext) {
            retry.retry()
            connect(resume)
        } else {
            lavakord.removeNode(this)
            error("Could not reconnect to websocket after to many attempts")
        }
    }

    private suspend fun onEvent(event: GatewayPayload) {
        LOG.trace { "Received event: $event" }
        when (event) {
            is GatewayPayload.PlayerUpdateEvent -> (lavakord.getLink(event.guildId).player as WebsocketPlayer).provideState(
                event.state
            )

            is GatewayPayload.StatsEvent -> {
                LOG.debug { "Received node statistics for $name: $event" }
                lastStatsEvent = event
            }

            is GatewayPayload.EmittedEvent -> {
                eventPublisher.tryEmit(event)
            }

            is GatewayPayload.ReadyEvent -> {
                sessionId = event.sessionId
                updateSession(UpdateSessionRequest(resumeKey, lavakord.options.link.resumeTimeout))
            }
        }
    }

    override fun close() {
        lavakord.launch {
            session.close(CloseReason(CloseReason.Codes.NORMAL, "Close requested by client"))
        }
    }

    internal companion object {
        private fun generateResumeKey(): String {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return (1..25)
                .map { allowedChars.random() }
                .joinToString("")
        }
    }

    private fun HttpRequestBuilder.addUrl() {
        val resources = lavakord.gatewayClient.plugin(Resources)
        url {
            takeFrom(this@NodeImpl.host)
            href(resources.resourcesFormat, V3Api.WebSocket(), this)
        }
    }
}
