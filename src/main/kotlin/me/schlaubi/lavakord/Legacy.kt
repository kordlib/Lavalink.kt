package me.schlaubi.lavakord

import dev.kord.core.Kord
import me.schlaubi.lavakord.kord.lavakord
import me.schlaubi.lavakord.kord.LavaKordOptions as NewLavaKordOptions
import me.schlaubi.lavakord.kord.MutableLavaKordOptions as NewMutableLavaKordOptions

/**
 * Creates a [LavaKord] instance for this [Kord] instance.
 *
 * @param configure a receiver configuring the [LavaKordOptions] instance used for configuration of this instance
 */
@Suppress("unused")
@Deprecated(
    "This is being renamed in favor of new LavaKord api",
    ReplaceWith("lavakord", "me.schlaubi.lavakord.kord.lavakord")
)
public fun Kord.lavalink(configure: me.schlaubi.lavakord.kord.MutableLavaKordOptions.() -> Unit = {}): LavaKord =
    lavakord(configure)

/**
 * Legacy alias to [NewLavaKordOptions].
 *
 * @see NewLavaKordOptions
 */
@Deprecated("Migrate to kord package", ReplaceWith("LavaKordOptions", "me.schlaubi.lavakord.kord.LavaKordOptions"))
public typealias LavaKordOptions = NewLavaKordOptions

/**
 * Legacy alias to [NewMutableLavaKordOptions].
 *
 * @see NewMutableLavaKordOptions
 */
@Deprecated(
    "Migrate to kord package",
    ReplaceWith("NewMutableLavaKordOptions", "me.schlaubi.lavakord.kord.NewMutableLavaKordOptions")
)
public typealias MutableLavaKordOptions = NewMutableLavaKordOptions
