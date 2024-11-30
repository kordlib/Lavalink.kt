package dev.schlaubi.lavakord.audio.internal

import dev.arbjerg.lavalink.protocol.v4.Message
import dev.arbjerg.lavalink.protocol.v4.SessionUpdate
import dev.arbjerg.lavalink.protocol.v4.Stats
import dev.arbjerg.lavalink.protocol.v4.toOmissible
import dev.schlaubi.lavakord.Plugin
import dev.schlaubi.lavakord.audio.Event
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.rest.getInfo
import dev.schlaubi.lavakord.rest.getVersion
import dev.schlaubi.lavakord.rest.routes.V4Api
import dev.schlaubi.lavakord.rest.updateSession
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.plugins.*
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.concurrent.Volatile
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal val LOG = KotlinLogging.logger { }

private data class SessionIdContainer(private var value: String? = null) : ReadWriteProperty<Any?, String> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String = value
        ?: error("WebSocket connection is not ready yet, please wait for the connection to finish")

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        this.value = value
    }
}

internal class NodeImpl(
    override val host: Url,
    override val name: String,
    override val authenticationHeader: String,
    override val lavakord: AbstractLavakord
) : Node {
    private val resumeTimeout = lavakord.options.link.resumeTimeout
    private val retry = lavakord.options.link.retry

    override var sessionId: String by SessionIdContainer()

    @Volatile
    override var available: Boolean = false
    override var lastStatsEvent: Stats? = null
    private var eventPublisher: MutableSharedFlow<Event> =
        MutableSharedFlow(extraBufferCapacity = Channel.UNLIMITED)
    private lateinit var session: DefaultClientWebSocketSession
    override val coroutineScope: CoroutineScope
        get() = lavakord

    override val events: SharedFlow<Event>
        get() = eventPublisher.asSharedFlow()

    internal suspend fun check() {
        val version = getVersion()
        val (_, _, _, _, _, _, _, plugins) = getInfo()
        if (!version.startsWith("4")) {
            val message = "Unsupported Lavalink version (${version} on node $name"
            if ("SNAPSHOT" in message) {
                LOG.warn { message }
            } else {
                error(message)
            }
        }
        val pluginMap = plugins.plugins.associate { (name, version) -> name to version }
        val installedPlugins = lavakord.options.plugins.plugins
        val installedPluginNames = lavakord.options.plugins
            .plugins.map(Plugin::name)
        installedPlugins.forEach { plugin ->
            val serverVersion = pluginMap[plugin.name]
            if (serverVersion != plugin.version) {
                LOG.warn { "Plugin ${plugin.name} was loaded with version ${plugin.version}, but the server uses $serverVersion" }
            }
        }
        pluginMap.forEach { (name) ->
            if (name !in installedPluginNames) {
                LOG.warn { "Plugin $name is installed on the server but not in Lavalink.kt" }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    internal suspend fun connect(doResume: Boolean = false, initialSessionId: String? = null) {
        val resume = doResume || initialSessionId != null
        sessionId = initialSessionId ?: sessionId
        session = try {
            connect(resume || initialSessionId != null) {
                addUrl()
                timeout {
                    requestTimeoutMillis = HttpTimeoutConfig.INFINITE_TIMEOUT_MS
                }
                header("Authorization", authenticationHeader)
                header("User-Id", lavakord.userId)
                header("Client-Name", "Lavalink.kt")
                if (resume) {
                    header("Session-Id", sessionId)
                }
            }
        } catch (e: ReconnectException) {
            reconnect(e.cause, resume)
            return
        }

        retry.reset()

        LOG.info { "Successfully connected to node: $name ($host)" }

        while (!session.incoming.isClosedForReceive) {
            try {
                onEvent(session.receiveDeserialized())
            } catch (ignored: ClosedReceiveChannelException) {
                break
            } catch (e: Exception) {
                LOG.warn(e) { "An exception occurred whilst decoding incoming websocket packet" }
                if (e::class.simpleName == "EOFException") {
                    reconnect(e, resume)
                }
            }
        }

        available = false
        val reason = session.closeReason.await()
        val resumeAgain = resume && reason?.knownReason != CloseReason.Codes.NORMAL
        if (resumeAgain) {
            LOG.warn { "$name disconnected from websocket for: $reason. Music will continue playing if we can reconnect within the next $resumeTimeout seconds" }
        } else {
            LOG.warn { "$name disconnected from websocket for: $reason. Not resuming." }
            if (lavakord.options.link.autoReconnect && lavakord.options.link.autoMigrateOnDisconnect) {
                lavakord.migrateFromDisconnectedNode(this)
            }
        }
        reconnect(resume = resumeAgain)
    }

    private suspend fun reconnect(e: Throwable? = null, resume: Boolean = false) {
        if (retry.hasNext) {
            LOG.error { "Exception whilst trying to connect: '${e?.message}'. Reconnecting" }
            retry.retry()
            connect(resume)
        } else {
            lavakord.removeNode(this)
            throw IllegalStateException("Could not reconnect to websocket after too many attempts", e)
        }
    }

    private suspend fun onEvent(eventRaw: JsonElement) {
        LOG.trace { "Received event: $eventRaw" }
        @Suppress("ReplaceNotNullAssertionWithElvisReturn")
        val op = eventRaw.jsonObject["op"]!!.jsonPrimitive.content
        val eventType = eventRaw.jsonObject["type"]?.jsonPrimitive?.content
        val providingPlugin = lavakord.options.plugins.plugins.firstOrNull {
            if (op == "event") {
                eventType in it.eventTypes
            } else {
                op in it.opCodes
            }
        }
        if (providingPlugin != null) {
            val event = with(providingPlugin) {
                eventRaw.decodeToEvent()
            }
            eventPublisher.tryEmit(event)
            return
        }
        val event = try {
            lavakord.json.decodeFromJsonElement<Message>(eventRaw)
        } catch (e: SerializationException) {
            LOG.warn(e) { "Could not parse event" }
        }
        when (event) {
            is Message.PlayerUpdateEvent -> {
                val link = lavakord.getLink(event.guildId) as AbstractLink

                if (event.state.connected && link.state == Link.State.CONNECTING) {
                    link.state = Link.State.CONNECTED
                } else if (!event.state.connected && link.state == Link.State.DISCONNECTING) {
                    link.state = Link.State.NOT_CONNECTED
                }

                (link.player as WebsocketPlayer).provideState(event.state)
            }

            is Message.EmittedEvent.WebSocketClosedEvent -> {
                // These codes represent an invalid session
                // See https://discord.com/developers/docs/topics/opcodes-and-status-codes#voice-voice-close-event-codes
                try {
                    if (event.code == 4004 || event.code == 4006 || event.code == 4009 || event.code == 4014) {
                        LOG.debug { "Node $name received close code ${event.code} for guild ${event.guildId}" }
                        lavakord.getLink(event.guildId).onDisconnected()
                    }
                } finally {
                    // Must still be emitted
                    eventPublisher.tryEmit(event.toEvent())
                }
            }

            is Message.StatsEvent -> {
                LOG.debug { "Received node statistics for $name: $event" }
                lastStatsEvent = event
            }

            is Message.EmittedEvent -> {
                eventPublisher.tryEmit(event.toEvent())
            }

            is Message.ReadyEvent -> {
                available = true
                sessionId = event.sessionId
                lavakord.onNewSession(this)
                updateSession(
                    SessionUpdate(
                        resuming = true.toOmissible(),
                        timeoutSeconds = lavakord.options.link.resumeTimeout
                            .toLong().toOmissible()
                    )
                )
            }
        }
    }

    override fun close() {
        available = false
        lavakord.launch {
            session.close(CloseReason(CloseReason.Codes.NORMAL, "Close requested by client"))
            if (lavakord.options.link.autoReconnect && lavakord.options.link.autoMigrateOnDisconnect) {
                lavakord.migrateFromDisconnectedNode(this@NodeImpl)
            }
        }
    }

    private fun HttpRequestBuilder.addUrl() {
        val resources = lavakord.gatewayClient.plugin(Resources)
        url {
            takeFrom(this@NodeImpl.host)
            href(resources.resourcesFormat, V4Api.WebSocket(), this)
        }
    }

    override fun toString() = "Node($name)"
}
