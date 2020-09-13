package me.schlaubi.lavakord

import com.gitlab.kordlib.core.Kord
import lavalink.client.io.Lavalink
import lavalink.client.io.Link
import me.schlaubi.lavakord.audio.KordLavaLink

/**
 * Creates a [Lavalink] instance for this [Kord] instance.
 *
 * @param configure a receiver configuring the [KordLinkOptions] instance used for configuration of this instance
 */
@Suppress("unused")
fun Kord.lavalink(configure: MutableKordLinkOptions.() -> Unit = {}): Lavalink<out Link> {
    val options = MutableKordLinkOptions().apply(configure).seal()
    return KordLavaLink(
        this,
        options,
        selfId.value,
        resources.shardCount
    )
}

/**
 * Interface representing options for Kordlink.
 *
 * @property autoReconnect Whether to auto-reconnect links or not
 */
interface KordLinkOptions {
    val autoReconnect: Boolean
}

/**
 * Mutable implementation of [KordLinkOptions].
 */
data class MutableKordLinkOptions(override var autoReconnect: Boolean = true) : KordLinkOptions {
    internal fun seal() = ImmutableKordLinkOptions(autoReconnect)
}

internal data class ImmutableKordLinkOptions(override val autoReconnect: Boolean) : KordLinkOptions
