package dev.kord.x.lavalink.audio.internal

import dev.kord.x.lavalink.audio.Link
import dev.kord.x.lavalink.audio.Node
import dev.kord.x.lavalink.audio.player.Player

/**
 * Abstract implementation of [Link].
 */
public abstract class AbstractLink(final override val node: Node, final override val guildId: Long) : Link {
    override val player: Player = WebsocketPlayer(node as NodeImpl, guildId)
    abstract override val lavakord: AbstractLavakord
    override var lastChannelId: Long? = null
    override var state: Link.State = Link.State.NOT_CONNECTED

    override suspend fun onDisconnected() {
        state = Link.State.NOT_CONNECTED
        (node as NodeImpl).send(GatewayPayload.DestroyCommand(guildId.toString()))
    }

    override suspend fun destroy() {
        val shouldDisconnect = state !== Link.State.DISCONNECTING && state !== Link.State.NOT_CONNECTED
        state = Link.State.DESTROYING
        if (shouldDisconnect) {
            disconnectAudio()
        }
        (node as NodeImpl).send(GatewayPayload.DestroyCommand(guildId.toString()))
        lavakord.removeDestroyedLink(this)
        state = Link.State.DESTROYED
    }
}
