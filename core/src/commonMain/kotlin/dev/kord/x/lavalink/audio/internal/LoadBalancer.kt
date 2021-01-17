package dev.kord.x.lavalink.audio.internal

import dev.kord.x.lavalink.LavaKord
import dev.kord.x.lavalink.audio.Node
import kotlin.math.pow

internal class LoadBalancer(private val penaltyProviders: List<PenaltyProvider>, private val lavakord: LavaKord) {

    fun determineBestNode(guildId: Long): Node {
        val leastPenalty = lavakord.nodes
            .asSequence()
            .filter(Node::available)
            .minByOrNull { calculatePenalties(it, penaltyProviders, guildId) }

        checkNotNull(leastPenalty) { "Node nodes available" }

        return leastPenalty
    }

    // Inspired by: https://github.com/Frederikam/Lavalink-Client/blob/master/src/main/java/lavalink/client/io/LavalinkLoadBalancer.java#L111
    private fun calculatePenalties(
        node: Node,
        penaltyProviders: List<PenaltyProvider>,
        guildId: Long
    ): Int {
        val playerPenalty: Int
        val cpuPenalty: Int
        val deficitFramePenalty: Int
        val nullFramePenalty: Int
        val customPenalties = penaltyProviders.sumBy { it.getPenalty(node, guildId) }

        val stats = (node as NodeImpl).lastStatsEvent
        if (stats == null) {
            playerPenalty = 0
            cpuPenalty = 0
            deficitFramePenalty = 0
            nullFramePenalty = 0
        } else {
            playerPenalty = stats.playingPlayers

            cpuPenalty = 1.05.pow(100 * stats.cpu.systemLoad).toInt() * 10 - 10
            if ((stats.frameStats != null) && stats.frameStats.deficit != 1) {
                deficitFramePenalty =
                    (1.03.pow((500f * (stats.frameStats.deficit.toFloat() / 3000f)).toDouble()) * 600 - 600).toInt()
                nullFramePenalty =
                    (1.03.pow((500f * (stats.frameStats.nulled.toFloat() / 3000f)).toDouble()) * 300 - 300).toInt() * 2
            } else {
                deficitFramePenalty = 0
                nullFramePenalty = 0
            }
        }
        return playerPenalty + cpuPenalty + deficitFramePenalty + nullFramePenalty + customPenalties
    }
}

/**
 * Interface to accept custom penalties for [Node]s.
 */
public fun interface PenaltyProvider {

    /**
     * This method allows for adding custom penalties to {@link LavalinkSocket nodes}, making it possible to
     * change how the node selection system works on a per-guild per-node basis.
     * By using the provided {@link LavalinkLoadBalancer.Penalties Penalties} class you can fetch default penalties like CPU or Players.
     *
     * @param node the [Node] to calculate penalties for
     * @param guildId the id of the guild a node is searched for
     * @see Node.lastStatsEvent
     * @return total penalty to add to this node.
     */
    public fun getPenalty(node: Node, guildId: Long): Int
}