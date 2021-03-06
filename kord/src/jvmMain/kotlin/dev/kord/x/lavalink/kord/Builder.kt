package dev.kord.x.lavalink.kord

import dev.kord.core.Kord
import dev.kord.x.lavalink.LavaKord
import dev.kord.x.lavalink.LavaKordOptions
import dev.kord.x.lavalink.MutableLavaKordOptions

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
        resources.shards.totalShards,
        options
    )
}
