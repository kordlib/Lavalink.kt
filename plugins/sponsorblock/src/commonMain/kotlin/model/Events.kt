package dev.schlaubi.lavakord.plugins.sponsorblock.model

import dev.schlaubi.lavakord.audio.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import dev.schlaubi.lavakord.plugins.sponsorblock.model.Event as LavakordEvent

@JsonClassDiscriminator("type")
@Serializable
@Suppress("unused")
internal sealed interface Event : SponsorblockEvent {
    val op: String

    @SerialName("SegmentsLoaded")
    @Serializable
    data class SegmentLoaded(
        override val op: String,
        override val guildId: ULong,
        override val segments: List<SponsorblockSegment>
    ) : LavakordEvent, SegmentsLoadedEvent

    @SerialName("SegmentSkipped")
    @Serializable
    data class SegmentSkipped(
        override val op: String,
        override val guildId: ULong,
        override val segment: SponsorblockSegment
    ) : LavakordEvent, SegmentSkippedEvent
}

/**
 * Representation of a Sponsorblock segment.
 *
 * @property category the [Category] of this segment
 * @property startMs the milliseconds of the position where the segment started
 * @property endMs the milliseconds of the position where the segment ended
 * @property start the [position][Duration] where the segment started
 * @property end the [position][Duration] where the segment ended
 */
@Serializable
public data class SponsorblockSegment(
    val category: Category,
    @SerialName("start")
    val startMs: Int,
    @SerialName("end")
    val endMs: Int
) {
    public val start: Duration
        get() = startMs.toDuration(DurationUnit.MILLISECONDS)
    public val end: Duration
        get() = endMs.toDuration(DurationUnit.MILLISECONDS)
}

/**
 * Super class for all sponsor block events.
 */
public interface SponsorblockEvent : Event

/**
 * Event fired when the segments for a track were loaded.
 *
 * @property segments the [segments][SponsorblockSegment] for this track.
 */
public interface SegmentsLoadedEvent : SponsorblockEvent {
    public val segments: List<SponsorblockSegment>
}

/**
 * Event fired when a segment was skipped.
 *
 * @property segment the [segment][SponsorblockSegment] which was skipped
 */
public interface SegmentSkippedEvent : SponsorblockEvent {

    public val segment: SponsorblockSegment
}
