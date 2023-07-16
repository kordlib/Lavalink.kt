package dev.schlaubi.lavakord.plugins.lavasrc

import dev.schlaubi.lavakord.Plugin
import kotlinx.serialization.json.Json

internal val json = Json {
    ignoreUnknownKeys = true
}

/**
 * Bindings for the [LavaSrc plugin](https://github.com/topi314/LavaSrc).
 *
 * ```kotlin
 * plugins {
 *   install(LavaSrc)
 * }
 * ```
 */
public object LavaSrc : Plugin {
    override val name: String = "lavasrc-plugin"
    override val version: String = "4.0.0-beta.3"
}
