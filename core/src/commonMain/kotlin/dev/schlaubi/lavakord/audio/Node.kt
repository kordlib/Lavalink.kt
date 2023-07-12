package dev.schlaubi.lavakord.audio

import dev.arbjerg.lavalink.protocol.v4.Stats
import dev.schlaubi.lavakord.LavaKord
import io.ktor.http.*
import io.ktor.utils.io.core.*

/**
 * Representation of a Lavalink node supporting rest calls.
 */
public interface RestNode {

    /**
     * The host of the node.
     *
     * @see Url
     */
    public val host: Url

    /**
     * The name of the node.
     *
     * @see LavaKord.addNode
     */
    public val name: String

    /**
     * The password for communicating with the node.
     */
    public val authenticationHeader: String

    /**
     * The [LavaKord] instance which created this node.
     */
    public val lavakord: LavaKord
}

/**
 * Representation of a Lavalink node, supporting rest calls and a websocket connection.
 */
public interface Node : RestNode, EventSource<Event>, Closeable {

    /**
     * Whether this node is currently available or not (e.g. reconnecting).
     */
    public val available: Boolean

    /**
     * The last [StatsEvent] received from this node or null if no event has been received yet.
     */
    public val lastStatsEvent: Stats?

    /**
     * The id of the current websocket session.
     */
    public val sessionId: String

}
