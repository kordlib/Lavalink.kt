package dev.schlaubi.lavakord.kord

import dev.kord.core.Kord
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.LavaKordOptions
import dev.schlaubi.lavakord.MutableLavaKordOptions

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
        options
    )
}
