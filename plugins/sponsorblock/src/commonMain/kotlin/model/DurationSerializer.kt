package dev.schlaubi.lavakord.plugins.sponsorblock.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

internal object DurationSerializer : KSerializer<Duration> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "com.github.topi314.sponsorblock.plugin.protocol.serialization.DurationonAsMilliseconds", PrimitiveKind.LONG
    )

    override fun deserialize(decoder: Decoder): Duration = decoder.decodeLong().toDuration(DurationUnit.MILLISECONDS)

    override fun serialize(encoder: Encoder, value: Duration): Unit = encoder.encodeLong(value.inWholeMilliseconds)
}
