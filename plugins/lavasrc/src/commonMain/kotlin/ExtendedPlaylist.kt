package dev.schlaubi.lavakord.plugins.lavasrc

import com.github.topi314.lavasrc.protocol.ExtendedPlaylistInfo
import dev.arbjerg.lavalink.protocol.v4.Playlist
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Provides the [ExtendedPlaylistInfo] for this [Playlist].
 */
public val Playlist.lavaSrcInfo: ExtendedPlaylistInfo
    get() = json.decodeFromJsonElement(JsonObject(pluginInfo))
