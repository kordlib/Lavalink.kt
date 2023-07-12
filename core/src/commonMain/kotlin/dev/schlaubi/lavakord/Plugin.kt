package dev.schlaubi.lavakord

import dev.arbjerg.lavalink.protocol.v4.Info
import dev.arbjerg.lavalink.protocol.v4.Plugin as RestPlugin
import dev.schlaubi.lavakord.audio.Event
import kotlinx.serialization.json.JsonElement

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
     * Op codes of events supported by this plugin.
     */
    public val opCodes: List<String>

    /**
     * Converts a [JsonElement] to an event specific plugin.
     */
    public fun JsonElement.decodeToEvent(): Event
}
