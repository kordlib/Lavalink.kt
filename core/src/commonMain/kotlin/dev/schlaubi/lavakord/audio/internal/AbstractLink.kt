package dev.schlaubi.lavakord.audio.internal

import dev.arbjerg.lavalink.protocol.v4.PlayerUpdate
import dev.arbjerg.lavalink.protocol.v4.VoiceState
import dev.arbjerg.lavalink.protocol.v4.toOmissible
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Link.State
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.player.Player
import dev.schlaubi.lavakord.audio.player.node
import dev.schlaubi.lavakord.rest.destroyPlayer
import dev.schlaubi.lavakord.rest.updatePlayer

/**
 * Abstract implementation of [Link].
 */
public abstract class AbstractLink(node: Node, final override val guildId: ULong) : Link {

    final override var node: Node = node
        private set

    override val player: Player = WebsocketPlayer(node as NodeImpl, guildId)
    abstract override val lavakord: AbstractLavakord
    override var lastChannelId: ULong? = null
    override var state: State = State.NOT_CONNECTED
    private var cachedVoiceState: VoiceState? = null

    override suspend fun onDisconnected() {
        state = State.NOT_CONNECTED
        node.destroyPlayer(guildId)
        cachedVoiceState = null
    }

    override suspend fun onNewSession(node: Node) {
        this.node = node
        val voiceState = cachedVoiceState

        state = if (voiceState != null) State.CONNECTING else State.NOT_CONNECTED

        try {
            (player as WebsocketPlayer).recreatePlayer(node as NodeImpl, voiceState)
            if (voiceState != null) {
                state = State.CONNECTED
            }
        } catch (e: Exception) {
            state = State.NOT_CONNECTED
            throw e
        }
    }

    override suspend fun destroy() {
        val shouldDisconnect = state !== State.DISCONNECTING && state !== State.NOT_CONNECTED
        state = State.DESTROYING
        if (shouldDisconnect) {
            disconnectAudio()
        }
        node.destroyPlayer(guildId)
        lavakord.removeDestroyedLink(this)
        state = State.DESTROYED
    }

    internal suspend fun onVoiceServerUpdate(update: VoiceState) {
        cachedVoiceState = update
        node.updatePlayer(guildId, request = PlayerUpdate(voice = update.toOmissible()))
    }
}
