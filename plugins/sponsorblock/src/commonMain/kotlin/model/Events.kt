package dev.schlaubi.lavakord.plugins.sponsorblock.model

import dev.schlaubi.lavakord.audio.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlin.time.Duration
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

    @SerialName("ChaptersLoaded")
    @Serializable
    data class ChaptersLoaded(
        override val op: String,
        override val guildId: ULong,
        override val chapters: List<YouTubeChapter>
    ) : LavakordEvent, ChaptersLoadedEvent

    @SerialName("ChapterStarted")
    @Serializable
    data class ChapterStarted(
        override val op: String,
        override val guildId: ULong,
        override val chapter: YouTubeChapter
    ) : LavakordEvent, ChapterStartedEvent
}

/**
 * Representation of a Sponsorblock segment.
 *
 * @property category the [Category] of this segment
 * @property start the [position][Duration] where the segment started
 * @property end the [position][Duration] where the segment ended
 */
@Serializable
public data class SponsorblockSegment(
    val category: Category,
    val start: @Serializable(with = DurationSerializer::class) Duration,
    val end: @Serializable(with = DurationSerializer::class) Duration
)

/**
 * Representation of a YouTube chapter.
 *
 * @property name the name of the chapter
 * @property start the [position][Duration] where the chapter started
 * @property end the [position][Duration] where the chapter ended
 * @property duration the [Duration] of the chapter
 */
@Serializable
public data class YouTubeChapter(
    val name: String,
    val start: @Serializable(with = DurationSerializer::class) Duration,
    val end: @Serializable(with = DurationSerializer::class) Duration,
    val duration: @Serializable(with = DurationSerializer::class) Duration
)

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

/**
 * Event fired when the segments for a track were loaded.
 *
 * @property chapters the [chapters][YouTubeChapter] for this track.
 */
public interface ChaptersLoadedEvent : SponsorblockEvent {
    public val chapters: List<YouTubeChapter>
}

/**
 * Event fired when a chapter began.
 *
 * @property chapter the [chapter][YouTubeChapter] which started.
 */
public interface ChapterStartedEvent : SponsorblockEvent {
    public val chapter: YouTubeChapter
}
