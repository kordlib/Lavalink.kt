package dev.schlaubi.lavakord

import dev.schlaubi.lavakord.audio.Event
import dev.schlaubi.lavakord.audio.TrackStartEvent
import dev.schlaubi.lavakord.audio.on
import dev.schlaubi.lavakord.audio.player.Player
import kotlinx.serialization.json.JsonElement
import dev.arbjerg.lavalink.protocol.v4.Plugin as RestPlugin

/**
 * Interface for a Lavalink plugin.
 */
public interface Plugin {

    /**
     * The name of the plugin listed in [RestPlugin.name]
     */
    public val name: String

    /**
     * The version of the plugin listed in [RestPlugin.version]
     */
    public val version: String

    /**
     * Op codes of events supported by [decodeToEvent].
     */
    public val opCodes: List<String>
        get() = emptyList()

    /**
     * Event types handled by [decodeToEvent].
     */
    public val eventTypes: List<String>
        get() = emptyList()

    /**
     * Converts a [JsonElement] to an event specific plugin.
     */
    public fun JsonElement.decodeToEvent(): Event {
        throw UnsupportedOperationException("Plugin was registered for op code but does not provide a deserializer")
    }
}
