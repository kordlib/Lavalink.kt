package dev.schlaubi.lavakord.audio.internal

import dev.schlaubi.lavakord.audio.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import mu.KotlinLogging

internal val LOG = KotlinLogging.logger { }

internal class NodeImpl(
    override val host: Url,
    override val name: String,
    override val authenticationHeader: String,
    override val lavakord: AbstractLavakord,
) : Node {

    private val resumeTimeout = lavakord.options.link.resumeTimeout
    private val retry = lavakord.options.link.retry

    private val resumeKey = generateResumeKey()
    override var available: Boolean = true
    override var lastStatsEvent: GatewayPayload.StatsEvent? = null
    private var eventPublisher: MutableSharedFlow<TrackEvent> =
        MutableSharedFlow(extraBufferCapacity = Channel.UNLIMITED)
    private lateinit var session: DefaultClientWebSocketSession
    override val coroutineScope: CoroutineScope
        get() = lavakord

    override val events: SharedFlow<TrackEvent>
        get() = eventPublisher.asSharedFlow()

    internal suspend fun connect(resume: Boolean = false) {
        session = try {
            connect(resume) {
                url(this@NodeImpl.host)
                header("Authorization", authenticationHeader)
                header("Num-Shards", lavakord.shardsTotal)
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
            lavakord.removeNode(this)
            error("Could not reconnect to websocket after to many attempts")
        }
    }

    internal suspend fun send(command: GatewayPayload) {
        val jsonCommand = lavakord.json.encodeToString(command)
        if (command is SanitizablePayload<*>) { // sanitize tokens or keys
            val sanitizedCommand by lazy { command.sanitize() }
            LOG.trace { "Sending command $sanitizedCommand" }
            LOG.trace { "Gateway >>> ${lavakord.json.encodeToString(sanitizedCommand)}" }
        } else {
            LOG.trace { "Sending command $command" }
            LOG.trace { "Gateway >>> $jsonCommand" }
        }
        session.outgoing.send(Frame.Text(jsonCommand))
    }

    private suspend fun onMessage(frame: Frame.Text) {
        val text = frame.readText()
        LOG.trace { "Gateway <<< $text" }
        val payload = try {
            lavakord.json.decodeFromString<GatewayPayload>(text)
        } catch (e: SerializationException) {
            LOG.warn(e) { "Error whilst handling websocket packet" }
            return
        }

        onEvent(payload)
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

                eventPublisher.tryEmit(emittedEvent)
            }
            else -> LOG.warn { "Received unexpected event: $event" }
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
}
