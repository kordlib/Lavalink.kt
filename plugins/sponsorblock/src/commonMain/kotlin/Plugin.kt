package dev.schlaubi.lavakord.plugins.sponsorblock

import dev.schlaubi.lavakord.Plugin
import dev.schlaubi.lavakord.audio.Event
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement
import dev.schlaubi.lavakord.plugins.sponsorblock.model.Event as SponsorblockRestEvent

/**
 * Bindings for the [Sponsorblock plugin](https://github.com/topi314/Sponsorblock-Plugin).
 *
 * ```kotlin
 * plugins {
 *   install(Sponsorblock)
 * }
 * ```
 */
public object Sponsorblock : Plugin {
    override val name: String
        // Topi messed up :rolling_eyes:
        get() = "lavasrc-plugin"
    override val version: String
        get() = "3.0.0-beta.1"

    override val eventTypes: List<String> = listOf("SegmentsLoaded", "SegmentSkipped")

    override fun JsonElement.decodeToEvent(): Event =
        Json.decodeFromJsonElement<SponsorblockRestEvent>(this)
}
