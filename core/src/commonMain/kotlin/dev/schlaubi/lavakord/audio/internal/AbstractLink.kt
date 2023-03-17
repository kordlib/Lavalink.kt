package dev.schlaubi.lavakord.audio.internal

import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.player.Player
import dev.schlaubi.lavakord.rest.destroyPlayer

/**
 * Abstract implementation of [Link].
 */
public abstract class AbstractLink(final override val node: Node, final override val guildId: ULong) : Link {
    override val player: Player = WebsocketPlayer(node as NodeImpl, guildId)
    abstract override val lavakord: AbstractLavakord
    override var lastChannelId: ULong? = null
    override var state: Link.State = Link.State.NOT_CONNECTED

    override suspend fun onDisconnected() {
        state = Link.State.NOT_CONNECTED
        node.destroyPlayer(guildId)
    }

    override suspend fun destroy() {
        val shouldDisconnect = state !== Link.State.DISCONNECTING && state !== Link.State.NOT_CONNECTED
        state = Link.State.DESTROYING
        if (shouldDisconnect) {
            disconnectAudio()
        }
        node.destroyPlayer(guildId)
        lavakord.removeDestroyedLink(this)
        state = Link.State.DESTROYED
    }
}
