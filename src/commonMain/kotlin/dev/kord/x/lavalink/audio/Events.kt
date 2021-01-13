@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED")

package dev.kord.x.lavalink.audio

import dev.kord.x.lavalink.RemoteTrackException
import dev.kord.x.lavalink.audio.StatsEvent.*
import dev.kord.x.lavalink.audio.TrackEndEvent.EndReason
import dev.kord.x.lavalink.audio.internal.GatewayPayload
import dev.kord.x.lavalink.audio.player.Track
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/**
 * Base class for all events.
 *
 * @property guildId the id of the guild the even got fired on
 * @property track the [Track] the even is about
 */
public sealed class TrackEvent {
    public abstract val guildId: Long
    public abstract val track: Track
}

/**
 * Even fired when a new track starts playing.
 *
 * @see TrackEvent
 */
public data class TrackStartEvent(override val guildId: Long, override val track: Track) :
    TrackEvent() {
    internal companion object {
        suspend operator fun invoke(event: GatewayPayload.EmittedEvent): TrackStartEvent {
            require(event.type == GatewayPayload.EmittedEvent.Type.TRACK_START_EVENT) { "Event needs to be track start event" }
            return TrackStartEvent(event.guildId.toLong(), Track.fromLavalink(event.track))
        }
    }
}

/**
 * Track fired when a track ended.
 *
 * @property reason the [EndReason] for this track ending
 * @see TrackEvent
 */
public data class TrackEndEvent(
    override val guildId: Long,
    override val track: Track,
    public val reason: EndReason
) :
    TrackEvent() {

    /**
     * Representation of a Track end reason.
     *
     *  @param mayStartNext Indicates whether a new track should be started on receiving this event.
     *  If this is false, either this event is already triggered because another track started (REPLACED)
     *  or because the player is stopped (STOPPED, CLEANUP).
     *
     *  Credit to lavaplayer: https://github.com/sedmelluq/lavaplayer/blob/master/main/src/main/java/com/sedmelluq/discord/lavaplayer/track/AudioTrackEndReason.java
     */
    public enum class EndReason(public val mayStartNext: Boolean) {
        /**
         * The Track finished playing.
         */
        FINISHED(true),

        /**
         * This means that the track failed to start, throwing an exception before providing any audio.
         */
        LOAD_FAILED(true),

        /**
         * The track was stopped due to the player being stopped by either calling stop() or playTrack(null).
         */
        STOPPED(false),

        /**
         * The track stopped playing because a new track started playing. Note that with this reason, the old track will still
         * play until either its buffer runs out or audio from the new track is available.
         */
        REPLACED(false),

        /**
         * The track was stopped because the cleanup threshold for the audio player was reached. This triggers when the amount
         * of time passed since the last call to AudioPlayer#provide() has reached the threshold specified in player manager
         * configuration. This may also indicate either a leaked audio player which was discarded, but not stopped.
         */
        CLEANUP(false);
    }

    /**
     * This means that the track itself emitted a terminator. This is usually caused by the track reaching the end,
     * however it will also be used when it ends due to an exception.
     */
    internal companion object {
        suspend operator fun invoke(event: GatewayPayload.EmittedEvent): TrackEndEvent {
            require(event.type == GatewayPayload.EmittedEvent.Type.TRACK_END_EVENT && event.reason != null) { "Event needs to be track end event" }
            return TrackEndEvent(
                event.guildId.toLong(),
                Track.fromLavalink(event.track),
                EndReason.valueOf(event.reason)
            )
        }
    }
}

/**
 * Event fired when an [RemoteTrackException] occurs whilst trying to play a track.
 *
 * @property exception the [RemoteTrackException] thrown
 * @see TrackEvent
 */
public data class TrackExceptionEvent(
    override val guildId: Long, override val track: Track, public val exception: RemoteTrackException
) : TrackEvent() {

    internal companion object {
        suspend operator fun invoke(event: GatewayPayload.EmittedEvent): TrackExceptionEvent {
            require(event.type == GatewayPayload.EmittedEvent.Type.TRACK_EXCEPTION_EVENT && event.error != null) { "Event has to be track exception event" }
            return TrackExceptionEvent(
                event.guildId.toLong(),
                Track.fromLavalink(event.track),
                RemoteTrackException(event.error)
            )
        }
    }
}

/**
 * Event that is fired when a track was started, but no audio frames from it have arrived in a long time
 *
 * @property threshold The wait threshold that was exceeded for this event to trigger
 * @see TrackEvent
 */
@OptIn(ExperimentalTime::class)
public data class TrackStuckEvent(
    override val guildId: Long, override val track: Track, public val threshold: Duration
) : TrackEvent() {

    internal companion object {
        suspend operator fun invoke(event: GatewayPayload.EmittedEvent): TrackStuckEvent {
            require(event.type == GatewayPayload.EmittedEvent.Type.TRACK_STUCK_EVENT && event.thresholdMs != null) { "Event has to be track stuck event" }
            return TrackStuckEvent(
                event.guildId.toLong(),
                Track.fromLavalink(event.track),
                event.thresholdMs.toDuration(DurationUnit.MILLISECONDS)
            )
        }
    }
}

/**
 * Event fired when Discord closes the websocket connection.
 *
 * @property code the websocket close code
 * @property reason the detailed reason why the connection was closed
 * @property byRemote whether the connection was closed by the remote or not
 */
public data class WebsocketClosedEvent(
    override val guildId: Long,
    public val code: Int,
    public val reason: String,
    public val byRemote: Boolean
) : TrackEvent() {
    override val track: Nothing
        get() = throw UnsupportedOperationException("Not supported by this event")

    internal companion object {
        operator fun invoke(event: GatewayPayload.EmittedEvent): WebsocketClosedEvent {
            require(event.type == GatewayPayload.EmittedEvent.Type.WEBSOCKET_CLOSED_EVENT && event.code != null && event.reason != null && event.byRemote != null) { "Event has to be track stuck event" }
            return WebsocketClosedEvent(
                event.guildId.toLong(),
                event.code,
                event.reason,
                event.byRemote
            )
        }
    }

}

/**
 * Event received regularly from [nodes][Node] to update node stats.
 *
 * @property players amount of players on this node
 * @property playingPlayers amount of playing players on this node
 * @property uptime the amount of milliseconds the node was running
 * @property memory the [Memory] statistics of this node
 * @property cpu the [Cpu] statistics of this node
 * @property frameStats the [FrameStats] statistics of this node (only present if players > 0)
 */
public interface StatsEvent {
    public val players: Int
    public val playingPlayers: Int
    public val uptime: Long
    public val memory: Memory
    public val cpu: Cpu
    public val frameStats: FrameStats?


    /**
     * Memory statistics of a node.
     *
     * @property free the amount of free memory in the Lavalink JVM (in bytes)
     * @property used the amount of used memory in the Lavalink JVM (in bytes)
     * @property allocated the amount of allocated memory to the Lavalink JVM (in bytes)
     * @property reservable the amount of maximal memory the Lavalink JVM will attempt touse (in bytes)
     */
    @Serializable
    public data class Memory(
        val free: Long,
        val used: Long,
        val allocated: Long,
        val reservable: Long
    )

    /**
     * Cpu statistics of a node.
     *
     * @property cores the amount of available CPU cores
     * @property systemLoad the percentage of cpu load (0.0-1.0)
     * @property lavalinkLoad the percentage of cpu load of the Lavalink process(0.0-1.0)
     */
    @Serializable
    public data class Cpu(
        val cores: Int,
        val systemLoad: Double,
        val lavalinkLoad: Double
    )

    /**
     * Statistics for frames of node for the last 60 seconds.
     *
     * @property sent amount of frames sent by this node
     * @property nulled amount of frames lost by this node
     * @property deficit deficiency of this nodes frames (See https://github.com/Frederikam/Lavalink/blob/master/LavalinkServer/src/main/java/lavalink/server/io/StatsTask.java#L111-L112)
     */
    @Serializable
    public data class FrameStats(
        val sent: Int,
        @Suppress("SpellCheckingInspection") val nulled: Int,
        val deficit: Int
    )
}
