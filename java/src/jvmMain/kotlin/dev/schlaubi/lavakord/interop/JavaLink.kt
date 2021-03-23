package dev.schlaubi.lavakord.interop

import dev.schlaubi.lavakord.InsufficientPermissionException
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Link.State
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.player.Player
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.CompletableFuture
import kotlin.coroutines.CoroutineContext

/**
 * Wrapper class for [Link] which replaces suspending functions by using [CompletableFuture].
 *
 * @property node the [Node] which is currently used to play songs (Can be changed in case one node crashes)
 * @property player a [Player] to play audio
 * @property lavakord the [LavaKord] instance which created this link
 * @property state the current [State] of this Link
 * @property guildId the id of the Guild this [Link] is connected to
 * @property lastChannelId the id of the last channel this Link is connected to
 */
public class JavaLink(internal val suspendingLink: Link) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = suspendingLink.lavakord.coroutineContext
    public val node: Node
        get() = suspendingLink.node
    public val player: JavaPlayer by lazy { JavaPlayer(suspendingLink.player) }
    public val lavakord: LavaKord
        get() = suspendingLink.lavakord
    public val state: State
        get() = suspendingLink.state
    public val guildId: Long
        get() = suspendingLink.guildId
    public val lastChannelId: Long?
        get() = suspendingLink.lastChannelId


    /**
     * Connects this link to the voice channel with the specified [voiceChannelId]
     *
     * Throws is only valid for kord implementation
     * @throws InsufficientPermissionException if the bot does not have the permission to join the voice channel or override user limit if needed
     * @throws NullPointerException If the [voiceChannelId] does not resolve to a valid voice channel
     */
    public fun connectAudio(voiceChannelId: Long): CompletableFuture<Void> =
        run { suspendingLink.connectAudio(voiceChannelId) }

    /**
     * Disconnects from the currently connected voice channel.
     */
    public fun disconnectAudio(): CompletableFuture<Void> = run { suspendingLink.disconnectAudio() }

    /**
     * Destroys this link (will no longer be usable).
     */
    public fun destroy(): CompletableFuture<Void> = run { suspendingLink.destroy() }
}
