package dev.schlaubi.lavakord.audio.internal

import dev.arbjerg.lavalink.protocol.v4.*
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.LavaKordOptions
import dev.schlaubi.lavakord.RestException
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.RestNode
import dev.schlaubi.lavakord.internal.HttpEngine
import dev.schlaubi.lavakord.internal.RestNodeImpl
import dev.schlaubi.lavakord.rest.updatePlayer
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.launch
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.modules.plus
import kotlin.collections.set
import kotlin.time.Duration.Companion.seconds

/**
 * Abstract implementation of [LavaKord].
 *
 * @property options [LavaKordOptions] object from builder
 * @property linksMap [Map] all [Link]s are stored in
 */
public abstract class AbstractLavakord internal constructor(
    override val userId: ULong,
    httpClientEngine: HttpClientEngineFactory<HttpClientEngineConfig>,
    override val options: LavaKordOptions
) : LavaKord {

    /**
     * Abstract implementation of [LavaKord].
     *
     * @property options [LavaKordOptions] object from builder
     * @property linksMap [Map] all [Link]s are stored in
     */
    public constructor(
        userId: ULong,
        options: LavaKordOptions
    ) : this(userId, HttpEngine, options)

    private val nodeCounter = atomic(0)
    private val nodesMap = mutableMapOf<String, Node>()
    protected val linksMap: MutableMap<ULong, Link> = mutableMapOf()

    internal val json = kotlinx.serialization.json.Json {
        ignoreUnknownKeys = true
        serializersModule = LavalinkSerializersModule + SerializersModule {
            contextual(Message.Serializer)
        }
    }

    private fun HttpClientConfig<*>.commonConfig() {
        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) = LOG.debug { message }
            }
        }

        install(Resources)
    }

    internal val restClient = HttpClient(httpClientEngine) {
        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) = LOG.trace { message }
            }
        }

        if (options.link.showTrace) {
            defaultRequest {
                url {
                    parameters.append("trace", "true")
                }
            }
        }

        HttpResponseValidator {
            handleResponseExceptionWithRequest { cause, request ->
                if (cause is ResponseException) {
                    val error = cause.response.body<Error>()

                    throw RestException(error, request)
                }
            }
        }

        expectSuccess = true

        commonConfig()
    }

    internal val gatewayClient = HttpClient(HttpEngine) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(json)
            pingInterval = 30.seconds.inWholeMilliseconds
        }

        expectSuccess = true

        install(HttpTimeout)
        commonConfig()
    }


    @Suppress("LeakingThis")
    private val loadBalancer: LoadBalancer = LoadBalancer(options.loadBalancer.penaltyProviders, this)

    override val nodes: List<Node>
        get() = nodesMap.values.toList()

    internal fun removeDestroyedLink(link: Link) = linksMap.remove(link.guildId)

    override fun getLink(guildId: ULong): Link {
        return linksMap.getOrPut(guildId) {
            val node = loadBalancer.determineBestNode(guildId) as NodeImpl
            buildNewLink(guildId, node)
        }
    }

    override fun addNode(serverUri: Url, password: String, name: String?) {
        if (name != null) {
            check(!nodesMap.containsKey(name)) { "Name is already in use" }
        }
        val finalName = name ?: "Lavalink_Node_#${nodeCounter.incrementAndGet()}"
        val node =
            NodeImpl(serverUri, finalName, password, this)
        nodesMap[finalName] = node
        launch {
            node.check()
            node.connect()
        }
    }

    override fun createRestNode(serverUri: Url, password: String, name: String?): RestNode = RestNodeImpl(
        serverUri,
        name ?: "Rest_only_node",
        password,
        this,
    )

    override fun removeNode(name: String) {
        val node = nodesMap.remove(name)
        requireNotNull(node) { "There is no node with that name" }
        node.close()
    }

    /**
     * Forwards an voice server update event to Lavalink.
     */
    protected suspend fun forwardVoiceEvent(
        link: Link,
        guildId: ULong,
        event: VoiceState
    ) {
        (link as AbstractLink).onVoiceServerUpdate(event)
    }

    /**
     * Abstract function to create a new [Link] for this [guild][guildId] using this [node].
     */
    protected abstract fun buildNewLink(guildId: ULong, node: Node): Link

    /** Called on websocket connect without resuming */
    internal suspend fun onNewSession(node: Node) {
        if (!options.link.autoReconnect) return
        linksMap.values.filter { it.node == node }.forEach {
            launch {
                it.onNewSession()
            }
        }
    }
}
