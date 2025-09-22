// THIS FILE IS AUTO-GENERATED, DO NOT EDIT!
@file:Suppress(names = arrayOf("IncorrectFormatting", "ReplaceArrayOfWithLiteral", "SpellCheckingInspection", "GrazieInspection"))

package dev.schlaubi.lavakord.plugins.sponsorblock.model

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
 * See [Category]s in the [Discord Developer Documentation](https://github.com/topi314/Sponsorblock-Plugin#segment-categories).
 */
@Serializable(with = Category.Serializer::class)
public sealed class Category(
    /**
     * The raw value used by Discord.
     */
    public val `value`: String,
) {
    final override fun equals(other: Any?): Boolean = this === other || (other is Category && this.value == other.value)

    final override fun hashCode(): Int = value.hashCode()

    final override fun toString(): String = if (this is Unknown) "Category.Unknown(value=$value)" else "Category.${this::class.simpleName}"

    /**
     * An unknown [Category].
     *
     * This is used as a fallback for [Category]s that haven't been added to Kord yet.
     */
    public class Unknown internal constructor(
        `value`: String,
    ) : Category(value)

    public object Sponsor : Category("sponsor")

    public object Selfpromo : Category("selfpromo")

    public object Interaction : Category("interaction")

    public object Intro : Category("intro")

    public object Outro : Category("outro")

    public object Preview : Category("preview")

    public object MusicOfftopic : Category("music_offtopic")

    public object Filler : Category("filler")

    internal object Serializer : KSerializer<Category> {
        override val descriptor: SerialDescriptor =
                PrimitiveSerialDescriptor("dev.schlaubi.lavakord.plugins.sponsorblock.model.Category", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, `value`: Category) {
            encoder.encodeString(value.value)
        }

        override fun deserialize(decoder: Decoder): Category = from(decoder.decodeString())
    }

    public companion object {
        /**
         * A [List] of all known [Category]s.
         */
        public val entries: List<Category> by lazy(mode = PUBLICATION) {
            listOf(
                Sponsor,
                Selfpromo,
                Interaction,
                Intro,
                Outro,
                Preview,
                MusicOfftopic,
                Filler,
            )
        }

        /**
         * Returns an instance of [Category] with [Category.value] equal to the specified [value].
         */
        public fun from(`value`: String): Category = when (value) {
            "sponsor" -> Sponsor
            "selfpromo" -> Selfpromo
            "interaction" -> Interaction
            "intro" -> Intro
            "outro" -> Outro
            "preview" -> Preview
            "music_offtopic" -> MusicOfftopic
            "filler" -> Filler
            else -> Unknown(value)
        }
    }
}
