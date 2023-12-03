package dev.schlaubi.lavakord.plugins.lavasrc

import com.github.topi314.lavasrc.protocol.ExtendedTrackInfo
import dev.arbjerg.lavalink.protocol.v4.Playlist
import dev.arbjerg.lavalink.protocol.v4.Track
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Provides the [ExtendedTrackInfo] for this [Playlist].
 */
public val Track.lavaSrcInfo: ExtendedTrackInfo
    get() = json.decodeFromJsonElement(JsonObject(pluginInfo))
