package dev.schlaubi.lavakord.audio

import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.audio.Link.State
import dev.schlaubi.lavakord.audio.player.Player
import kotlinx.coroutines.CoroutineScope

/**
 * Representation of a link between a Guild and a Lavalink node.
 *
 * @property node the [Node] which is currently used to play songs (Can be changed in case one node crashes)
 * @property player a [Player] to play audio
 * @property lavakord the [LavaKord] instance which created this link
 * @property state the current [State] of this Link
 * @property guildId the id of the Guild this [Link] is connected to
 * @property lastChannelId the id of the last channel this Link is connected to
 */
public interface Link : CoroutineScope {
    public val node: Node
    public val player: Player
    public val lavakord: LavaKord
    public val state: State
    public val guildId: ULong
    public val lastChannelId: ULong?

    /**
     * Connects this link to the voice channel with the specified [voiceChannelId]
     *
     * Throws is only valid for kord implementation
     * @throws dev.schlaubi.lavakord.InsufficientPermissionException if the bot does not have the permission to join the voice channel or override user limit if needed
     * @throws NullPointerException If the [voiceChannelId] does not resolve to a valid voice channel
     */
    public suspend fun connectAudio(voiceChannelId: ULong)

    /**
     * Connects this link to the voice channel with the specified [voiceChannelId].
     */
    public suspend fun connect(voiceChannelId: String): Unit = connectAudio(voiceChannelId.toULong())

    /**
     * Disconnects from the currently connected voice channel.
     */
    public suspend fun disconnectAudio()

    /**
     * Internal method that is called when the Discord voice states updates so the bot is no longer in a voice channel.
     */
    public suspend fun onDisconnected()

    /**
     * Called internally when this link is connected or reconnected to a new node without resuming, thereby creating a
     * new session. This function may also be used if the link is moved to a new session.
     * @param node The node that was connected to, which may be potentially different from the previously connected node
     */
    public suspend fun onNewSession(node: Node)

    /**
     * Destroys this link (will no longer be usable).
     */
    public suspend fun destroy()

    /**
     * Representation of different Link states.
     */
    public enum class State {
        /**
         * Default, means we are not trying to use voice at all
         */
        NOT_CONNECTED,

        /**
         * Waiting for VOICE_SERVER_UPDATE
         */
        CONNECTING,

        /**
         * We have dispatched the voice server info to the server, and it should (soon) be connected.
         */
        CONNECTED,

        /**
         * Waiting for confirmation from Discord that we have connected
         */
        DISCONNECTING,

        /**
         * This [Link] is being destroyed
         */
        DESTROYING,

        /**
         * This [Link] has been destroyed and will soon (if not already) be unmapped from [LavaKord]
         */
        DESTROYED
    }
}
