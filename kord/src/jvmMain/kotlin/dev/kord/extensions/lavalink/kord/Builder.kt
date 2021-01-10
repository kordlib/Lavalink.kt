package dev.kord.extensions.lavalink.kord

import dev.kord.core.Kord
import dev.kord.extensions.lavalink.LavaKord
import dev.kord.extensions.lavalink.LavaKordOptions
import dev.kord.extensions.lavalink.MutableLavaKordOptions

/**
 * Creates a [LavaKord] instance for this [Kord] instance.
 *
 * @param configure a receiver configuring the [LavaKordOptions] instance used for configuration of this instance
 */
public fun Kord.lavakord(configure: MutableLavaKordOptions.() -> Unit = {}): LavaKord {
    val options = MutableLavaKordOptions().apply(configure).seal()
    return KordLavaKord(
        this,
        this.selfId.value,
        resources.shardCount,
        options
    )
}
