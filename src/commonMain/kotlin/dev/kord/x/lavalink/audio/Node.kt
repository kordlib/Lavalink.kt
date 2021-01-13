package dev.kord.x.lavalink.audio

import dev.kord.x.lavalink.LavaKord
import io.ktor.http.*
import io.ktor.utils.io.core.*

/**
 * Representation of a Lavalink node.
 */
public interface Node : EventSource<TrackEvent>, Closeable {

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
     * Whether this node is currently available or not (e.g. reconnecting).
     */
    public val available: Boolean

    /**
     * The last [StatsEvent] received from this node or null if no event has been received yet.
     */
    public val lastStatsEvent: StatsEvent?
}
