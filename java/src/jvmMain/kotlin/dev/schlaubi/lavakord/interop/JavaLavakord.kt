package dev.schlaubi.lavakord.interop

import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.LavaKordOptions
import dev.schlaubi.lavakord.audio.Node
import java.net.URI

/**
 * Java interface for [LavaKord].
 * @see createJavaInterface
 */
public class JavaLavakord(private val parent: LavaKord) {
    /**
     * @see LavaKord.nodes
     */
    public val nodes: List<Node>
        get() = parent.nodes

    /**
     * @see LavaKord.userId
     */
    public val userId: Long
        get() = parent.userId

    /**
     * @see LavaKord.shardsTotal
     */
    public val shardsTotal: Int
        get() = parent.shardsTotal

    /**
     * @see LavaKord.options
     */
    public val options: LavaKordOptions
        get() = parent.options

    /**
     * @see LavaKord.getLink
     *
     * @see JavaLink
     */
    public fun getLink(guildId: Long): JavaLink {
        val link = parent.getLink(guildId)

        return JavaLink(link)
    }

    /**
     * @see LavaKord.addNode
     */
    @JvmOverloads
    public fun addNode(serverUri: URI, password: String, name: String? = null) {
        parent.addNode(serverUri.toString(), password, name)
    }

    /**
     * @see LavaKord.addNode
     */
    @JvmOverloads
    public fun addNode(serverUri: String, password: String, name: String? = null) {
        parent.addNode(serverUri, password, name)
    }

    /**
     * @see LavaKord.removeNode
     */
    public fun removeNode(name: String): Unit = parent.removeNode(name)

    /**
     * @see LavaKord.removeNode
     */
    public fun removeNode(node: Node): Unit = parent.removeNode(node)
}
