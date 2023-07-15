package dev.schlaubi.lavakord.plugins.lavasrc

import dev.arbjerg.lavalink.protocol.v4.Playlist
import dev.schlaubi.lavakord.Plugin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement

private val json = Json {
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

/**
 * Provides the [ExtendedPlaylistInfo] for this [Playlist].
 */
public val Playlist.lavaSrcInfo: ExtendedPlaylistInfo
    get() = json.decodeFromJsonElement(JsonObject(pluginInfo))

/**
 * The extended playlist info provided by LavaSrc.
 *
 * @property type the type of the playlist
 * @property url the url of the playlist
 * @property artworkUrl the artwork url of the playlist
 * @property author the name of the author of the playlist
 */
@Serializable
public data class ExtendedPlaylistInfo(
    val type: Type,
    val url: String,
    val artworkUrl: String,
    val author: String
) {
    /**
     * The type of the originating track list.
     */
    @Serializable
    public enum class Type {
        /**
         * A playlist from a music service.
         */
        @SerialName("playlist")
        PLAYLIST,

        /**
         * An album listed on a music service.
         */
        @SerialName("album")
        ALBUM,

        /**
         * An auto-generated playlist about an author from a music service.
         */
        @SerialName("artist")
        ARTIST,

        /**
         * Recommendations from a music service (currently only Spotify).
         */
        @SerialName("recommendations")
        RECOMMENDATIONS
    }
}
