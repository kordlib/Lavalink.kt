package me.schlaubi.lavakord

import dev.kord.x.lavalink.LavaKordOptions as NewLavaKordOptions
import dev.kord.x.lavalink.MutableLavaKordOptions as NewMutableLavaKordOptions

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
