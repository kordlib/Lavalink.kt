package dev.schlaubi.lavakord.audio.internal

import dev.arbjerg.lavalink.protocol.v4.Message
import dev.arbjerg.lavalink.protocol.v4.SessionUpdate
import dev.arbjerg.lavalink.protocol.v4.Stats
import dev.arbjerg.lavalink.protocol.v4.toOmissible
import dev.schlaubi.lavakord.Plugin
import dev.schlaubi.lavakord.audio.Event
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.rest.getInfo
import dev.schlaubi.lavakord.rest.getVersion
import dev.schlaubi.lavakord.rest.routes.V4Api
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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mu.KotlinLogging
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
    override val lavakord: AbstractLavakord,
) : Node {

    private val resumeTimeout = lavakord.options.link.resumeTimeout
    private val retry = lavakord.options.link.retry

    override var sessionId: String by SessionIdContainer()
    override var available: Boolean = true
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
        if(!version.startsWith("4")) {
            val message = "Unsupported Lavalink version (${version} on node $name"
            if ("SNAPSHOT" in message){
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
    internal suspend fun connect(resume: Boolean = false) {
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
                    header("Session-Id", sessionId)
                }
            }
        } catch (e: ReconnectException) {
            reconnect(e.cause, resume)
            return
        }

        retry.reset()

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
            LOG.warn(e) {"Could not parse event"}
        }
        when (event) {
            is Message.PlayerUpdateEvent -> (lavakord.getLink(event.guildId).player as WebsocketPlayer)
                .provideState(event.state)

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
        lavakord.launch {
            session.close(CloseReason(CloseReason.Codes.NORMAL, "Close requested by client"))
        }
    }

    private fun HttpRequestBuilder.addUrl() {
        val resources = lavakord.gatewayClient.plugin(Resources)
        url {
            takeFrom(this@NodeImpl.host)
            href(resources.resourcesFormat, V4Api.WebSocket(), this)
        }
    }
}
