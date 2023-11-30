package dev.schlaubi.lavakord.plugins.lavasearch

import dev.schlaubi.lavakord.Plugin

/**
 * Bindings for the [LavaSrc plugin](https://github.com/topi314/LavaSearch).
 *
 * ```kotlin
 * plugins {
 *   install(LavaSearch)
 * }
 * ```
 */
public object LavaSearch : Plugin {
    override val name: String = "lavasearch-plugin"
    override val version: String = "4.0.0-beta.3"
}
