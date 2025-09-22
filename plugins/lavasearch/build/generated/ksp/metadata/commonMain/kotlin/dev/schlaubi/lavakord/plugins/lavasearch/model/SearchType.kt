// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.schlaubi.lavakord.plugins.lavasearch.model

import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 *
 *
 * See [SearchType]s in the [Discord Developer Documentation](https://github.com/topi314/LavaSearch?tab=readme-ov-file#api).
 */
@Serializable(with = SearchType.Serializer::class)
public sealed class SearchType(
    /**
     * The raw value used by Discord.
     */
    public val `value`: String,
) {
    final override fun equals(other: Any?): Boolean = this === other || (other is SearchType && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "SearchType.Unknown(value=$value)" else "SearchType.${this::class.simpleName}"

    /**
     * An unknown [SearchType].
     *
     * This is used as a fallback for [SearchType]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: String,
    ) : SearchType(value)

    /**
     * Tracks.
     */
    public object Track : SearchType("track")

    /**
     * Album.
     */
    public object Album : SearchType("album")

    /**
     * Artists.
     */
    public object Artist : SearchType("artist")

    /**
     * Playlists.
     */
    public object Playlist : SearchType("playlist")

    /**
     * Search suggestions.
     */
    public object Text : SearchType("text")

    internal object Serializer : KSerializer<SearchType> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.schlaubi.lavakord.plugins.lavasearch.model.SearchType", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, `value`: SearchType) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): SearchType = from(decoder.decodeString())
    }

    public companion object {
        /**
         * A [List] of all known [SearchType]s.
         */
        public val entries: List<SearchType> by lazy(mode = PUBLICATION) {
            listOf(
                Track,
                Album,
                Artist,
                Playlist,
                Text,
            )
        }

        /**
         * Returns an instance of [SearchType] with [SearchType.value] equal to the specified [value].
         */
        public fun from(`value`: String): SearchType = when (value) {
            "track" -> Track
            "album" -> Album
            "artist" -> Artist
            "playlist" -> Playlist
            "text" -> Text
            else -> Unknown(value)
        }
    }
}
