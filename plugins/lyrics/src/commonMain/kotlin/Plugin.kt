package dev.schlaubi.lavakord.plugins.lyrics

import dev.schlaubi.lavakord.Plugin
import kotlinx.serialization.json.Json

/**
 * Bindings for the [Lyrics plugin](https://github.com/DRSchlaubi/lyrics.kt).
 *
 * ```kotlin
 * plugins {
 *   install(Lyrics)
 * }
 * ```
 */
public object Lyrics : Plugin {
    override val name: String = "lyrics"
    override val version: String = "2.0.0"
}
