package dev.kord.x.lavalink.audio.internal

import dev.kord.x.lavalink.LavaKord
import dev.kord.x.lavalink.LavaKordOptions
import dev.kord.x.lavalink.audio.DiscordVoiceServerUpdateData
import dev.kord.x.lavalink.audio.Link
import dev.kord.x.lavalink.audio.Node
import dev.kord.x.lavalink.computeIfAbsent
import dev.kord.x.lavalink.internal.HttpEngine
import dev.kord.x.lavalink.rest.RoutePlannerModule
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.launch

/**
 * Abstract implementation of [LavaKord].
 *
 * @property options [LavaKordOptions] object from builder
 * @property linksMap [Map] all [Link]s are stored in
 */
public abstract class AbstractLavakord internal constructor(
    override val userId: Long,
    override val shardsTotal: Int,
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
        userId: Long,
        shardsTotal: Int,
        options: LavaKordOptions
    ) : this(userId, shardsTotal, HttpEngine, options)

    private val nodeCounter = atomic(0)
    private val nodesMap = mutableMapOf<String, Node>()
    protected val linksMap: MutableMap<Long, Link> = mutableMapOf()

    internal val restClient = HttpClient(httpClientEngine) {
        install(JsonFeature) {
            val json = kotlinx.serialization.json.Json {
                serializersModule = RoutePlannerModule
                classDiscriminator = "class"
            }

            serializer = KotlinxSerializer(json)
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) = dev.kord.x.lavalink.rest.LOG.trace { message }
            }
        }

        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) = dev.kord.x.lavalink.rest.LOG.debug { message }
            }
        }
    }

    @OptIn(KtorExperimentalAPI::class)
    internal val gatewayClient = HttpClient(HttpEngine) {
        install(WebSockets)

        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) = LOG.debug { message }
            }
        }
    }


    @Suppress("LeakingThis")
    private val loadBalancer: LoadBalancer = LoadBalancer(options.loadBalancer.penaltyProviders, this)

    override val nodes: List<Node>
        get() = nodesMap.values.toList()

    internal fun removeDestroyedLink(link: Link) = linksMap.remove(link.guildId)

    override fun getLink(guildId: Long): Link {
        return linksMap.computeIfAbsent(guildId) {
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
            node.connect()
        }
    }

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
        guildId: String,
        sessionId: String,
        event: DiscordVoiceServerUpdateData
    ) {
        (link.node as NodeImpl).send(
            GatewayPayload.VoiceUpdateCommand(
                guildId,
                sessionId,
                event
            )
        )
    }

    /**
     * Abstract function to create a new [Link] for this [guild][guildId] using this [node].
     */
    protected abstract fun buildNewLink(guildId: Long, node: Node): Link
}
