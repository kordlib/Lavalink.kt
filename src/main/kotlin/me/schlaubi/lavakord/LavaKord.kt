package me.schlaubi.lavakord

import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import me.schlaubi.lavakord.audio.Link
import me.schlaubi.lavakord.audio.Node

/**
 * Representation of a Lavalink cluster.
 *
 * @property nodes list of [Node]s in this cluster
 * @property userId the id of the Discord bot user
 * @property shardsTotal the total amount of shards of the Discord bot
 */
public interface LavaKord : CoroutineScope {
    public val nodes: List<Node>
    public val userId: Long
    public val shardsTotal: Int

    /**
     * Returns the corresponding [Link] for the [guildId].
     */
    public fun getLink(guildId: Long): Link

    /**
     * Returns the corresponding [Link] for the [guildId].
     */
    public fun getLink(guildId: String): Link = getLink(guildId.toLong())

    /**
     * Adds a new node to this cluster.
     *
     * @param serverUri the uri to connect to
     * @param password the lavalink node password
     * @param name a optional name for the node
     */
    public fun addNode(serverUri: String, password: String, name: String? = null): Unit =
        addNode(Url(serverUri), password, name)

    /**
     * Adds a new node to this cluster.
     *
     * @param serverUri the uri to connect to
     * @param password the lavalink node password
     * @param name a optional name for the node
     */
    public fun addNode(serverUri: Url, password: String, name: String? = null)

    /**
     * Removes a node from the cluster by it's [name].
     */
    public fun removeNode(name: String)
}
