package dev.schlaubi.lavakord.audio.internal

import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.audio.Node
import kotlin.math.pow

public class LoadBalancer(
    private val penaltyProviders: List<PenaltyProvider>,
    private val lavakord: LavaKord
) {

    internal fun determineBestNode(guildId: ULong): Node? = lavakord.nodes
        .asSequence()
        .filter(Node::available)
        .minByOrNull { calculatePenalties(it, guildId).sum }

    /**
     * Calculate the penalties for a given guild
     * Adapted from https://github.com/freyacodes/Lavalink-Client/blob/master/src/main/java/lavalink/client/io/LavalinkLoadBalancer.java#L111
     */
    public fun calculatePenalties(
        node: Node,
        guildId: ULong
    ): Penalties {
        val playerPenalty: Int
        val cpuPenalty: Int
        val deficitFramePenalty: Int
        val nullFramePenalty: Int
        val customPenalties = penaltyProviders.sumOf { it.getPenalty(node, guildId) }

        val stats = (node as NodeImpl).lastStatsEvent
        if (stats == null) {
            playerPenalty = 0
            cpuPenalty = 0
            deficitFramePenalty = 0
            nullFramePenalty = 0
        } else {
            playerPenalty = stats.playingPlayers

            cpuPenalty = 1.05.pow(100 * stats.cpu.systemLoad).toInt() * 10 - 10
            if ((stats.frameStats != null) && stats.frameStats?.deficit != 1) {
                deficitFramePenalty =
                    (1.03.pow(((500f * (stats.frameStats?.deficit?.toFloat() ?: (0 / 3000f)))).toDouble()) * 600 - 600).toInt()
                nullFramePenalty =
                    (1.03.pow(((500f * (stats.frameStats?.nulled?.toFloat() ?: (0 / 3000f)))).toDouble()) * 300 - 300).toInt() * 2
            } else {
                deficitFramePenalty = 0
                nullFramePenalty = 0
            }
        }
        return Penalties(playerPenalty, cpuPenalty, deficitFramePenalty, nullFramePenalty, customPenalties)
    }
}

/** Result of penalties used for load balancing */
public data class Penalties(
    /** Penalty due to number of players */
    val playerPenalty: Int,
    /** Penalty due to high CPU */
    val cpuPenalty: Int,
    /** Penalty due to Lavalink struggling to send frames */
    val deficitFramePenalty: Int,
    /** Penalty due to Lavaplayer struggling to provide frames */
    val nullFramePenalty: Int,
    /** Penalties from [PenaltyProvider]*/
    val customPenalties: Int
) {
    /** The sum of all penalties */
    val sum: Int = playerPenalty + cpuPenalty + deficitFramePenalty + nullFramePenalty + customPenalties
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
    public fun getPenalty(node: Node, guildId: ULong): Int
}
