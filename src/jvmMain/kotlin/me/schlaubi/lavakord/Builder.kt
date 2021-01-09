package me.schlaubi.lavakord

import dev.kord.core.Kord
import dev.kord.extensions.lavalink.LavaKord
import dev.kord.extensions.lavalink.kord.lavakord
import dev.kord.extensions.lavalink.LavaKordOptions as NewLavaKordOptions
import dev.kord.extensions.lavalink.MutableLavaKordOptions as NewMutableLavaKordOptions

/**
 * Creates a [LavaKord] instance for this [Kord] instance.
 *
 * @param configure a receiver configuring the [LavaKordOptions] instance used for configuration of this instance
 */
@Suppress("unused")
@Deprecated(
    "This is being renamed in favor of new LavaKord api",
    ReplaceWith("lavakord", "dev.kord.extensions.lavalink.kord.lavakord")
)
public fun Kord.lavalink(configure: dev.kord.extensions.lavalink.MutableLavaKordOptions.() -> Unit = {}): LavaKord =
    lavakord(configure)

/**
 * Legacy alias to [NewLavaKordOptions].
 *
 * @see NewLavaKordOptions
 */
@Deprecated(
    "Migrate to kord package",
    ReplaceWith("LavaKordOptions", "dev.kord.extensions.lavalink.kord.LavaKordOptions")
)
public typealias LavaKordOptions = NewLavaKordOptions

/**
 * Legacy alias to [NewMutableLavaKordOptions].
 *
 * @see NewMutableLavaKordOptions
 */
@Deprecated(
    "Migrate to kord package",
    ReplaceWith("NewMutableLavaKordOptions", "dev.kord.extensions.lavalink.kord.NewMutableLavaKordOptions")
)
public typealias MutableLavaKordOptions = NewMutableLavaKordOptions
