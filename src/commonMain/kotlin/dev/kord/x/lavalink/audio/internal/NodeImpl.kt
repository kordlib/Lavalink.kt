package dev.kord.x.lavalink.audio.internal

import dev.kord.x.lavalink.LavaKord
import dev.kord.x.lavalink.audio.*
import dev.kord.x.lavalink.internal.HttpEngine
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.network.sockets.*
import io.ktor.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import mu.KotlinLogging

private val LOG = KotlinLogging.logger { }

@OptIn(ExperimentalCoroutinesApi::class)
internal class NodeImpl(
    override val host: Url,
    override val name: String,
    override val authenticationHeader: String,
    private val lavaKord: LavaKord,
) : Node {

    private val resumeTimeout = lavaKord.options.link.resumeTimeout
    private val retry = lavaKord.options.link.retry

    private val resumeKey = generateResumeKey()
    override var available: Boolean = true
    override var lastStatsEvent: GatewayPayload.StatsEvent? = null
    private var eventPublisher: BroadcastChannel<TrackEvent> = BroadcastChannel(1)
    private lateinit var session: DefaultClientWebSocketSession
    override val coroutineScope: CoroutineScope
        get() = lavaKord

    @OptIn(FlowPreview::class)
    override val events: Flow<TrackEvent>
        get() = eventPublisher.asFlow().buffer(Channel.UNLIMITED)

    @OptIn(InternalCoroutinesApi::class)
    internal suspend fun connect(resume: Boolean = false) {
        session = try {
            client.webSocketSession {
                url(this@NodeImpl.host)
                header("Authorization", authenticationHeader)
                header("Num-Shards", lavaKord.shardsTotal)
                header("User-Id", lavaKord.userId)
                header("Client-Name", "Lavakord")
                if (resume) {
                    header("Resume-Key", resumeKey)
                }
            }
        } catch (e: ServerResponseException) {
            reconnect(
                IllegalArgumentException(
                    "The provided server responded with an invalid response code",
                    e
                ), resume
            )
            return
        } catch (e: ConnectTimeoutException) {
            reconnect(IllegalStateException("The connection to the node timed out", e), resume)
            return
        } catch (e: ClientRequestException) {
            reconnect(e, resume)
            return
        }
        retry.reset()
        available = true

        LOG.debug { "Succesfully connected to node: $name ($host)" }

        send(GatewayPayload.ConfigureResumingCommand(resumeKey, resumeTimeout))

        for (message in session.incoming) {
            when (message) {
                is Frame.Text -> onMessage(message)
                else -> {
                    LOG.warn { "Received unexpected websocket frame: $message" }
                }
            }
        }
        val reason = session.closeReason.await()
        if (reason?.knownReason == CloseReason.Codes.NORMAL) return
        available = false
        LOG.warn { "Disconnected from websocket for: $reason. Music will continue playing if we can reconnect within the next $resumeTimeout seconds" }
        reconnect(resume = true)
    }

    private suspend fun reconnect(e: Throwable? = null, resume: Boolean = false) {
        LOG.error(e) { "Error whilst trying to connect. Reconnecting" }
        if (retry.hasNext) {
            retry.retry()
            connect(resume)
        } else {
            error("Could not reconnect to websocket after to many attempts")
        }
    }

    internal suspend fun send(command: GatewayPayload) {
        LOG.debug { "Sending command: $command" }
        session.outgoing.send(Frame.Text(json.encodeToString(command)))
    }

    private suspend fun onMessage(frame: Frame.Text) {
        val text = frame.readText()
        LOG.debug { "Received frame: $text" }
        val payload = try {
            json.decodeFromString<GatewayPayload>(text)
        } catch (e: SerializationException) {
            LOG.warn(e) { "Error whilst handling websocket packet" }
            return
        }

        onEvent(payload)
    }

    private suspend fun onEvent(event: GatewayPayload) {
        LOG.debug { "Received event: $event" }
        when (event) {
            is GatewayPayload.PlayerUpdateEvent -> (lavaKord.getLink(event.guildId).player as WebsocketPlayer).provideState(
                event.state
            )
            is GatewayPayload.StatsEvent -> {
                LOG.debug { "Received node statistics for $name: $event" }
                lastStatsEvent = event
            }
            is GatewayPayload.EmittedEvent -> {
                val emittedEvent = when (event.type) {
                    GatewayPayload.EmittedEvent.Type.TRACK_START_EVENT ->
                        TrackStartEvent(event)
                    GatewayPayload.EmittedEvent.Type.TRACK_END_EVENT ->
                        TrackEndEvent(event)
                    GatewayPayload.EmittedEvent.Type.TRACK_EXCEPTION_EVENT ->
                        TrackExceptionEvent(event)
                    GatewayPayload.EmittedEvent.Type.TRACK_STUCK_EVENT ->
                        TrackStuckEvent(event)
                    GatewayPayload.EmittedEvent.Type.WEBSOCKET_CLOSED_EVENT -> WebsocketClosedEvent(event)
                }

                eventPublisher.send(emittedEvent)
            }
            else -> LOG.warn { "Received unexpected event: $event" }
        }
    }

    override fun close() {
        lavaKord.launch {
            session.close(CloseReason(CloseReason.Codes.NORMAL, "Close requested by client"))
        }
    }

    internal companion object {
        private val json = kotlinx.serialization.json.Json {
            encodeDefaults = false
            classDiscriminator = "op"
        }

        @OptIn(KtorExperimentalAPI::class)
        private val client = HttpClient(HttpEngine) {
            install(WebSockets)

            install(JsonFeature) {
                serializer = KotlinxSerializer(json)
            }
        }

        private fun generateResumeKey(): String {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return (1..25)
                .map { allowedChars.random() }
                .joinToString("")
        }
    }
}
