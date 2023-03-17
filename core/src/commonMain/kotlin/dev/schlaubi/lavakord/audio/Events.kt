@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED")

package dev.schlaubi.lavakord.audio

import dev.schlaubi.lavakord.Exception
import dev.schlaubi.lavakord.audio.StatsEvent.*
import dev.schlaubi.lavakord.audio.TrackEndEvent.EndReason
import dev.schlaubi.lavakord.audio.player.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import dev.schlaubi.lavakord.audio.on as defaultOn

/**
 * Bae class for all events.
 *
 * @property guildId the id of the guild the even got fired on
 */
public interface Event {
    public val guildId: ULong
}

/**
 * Base class for events for a Track.
 *
 * @property guildId the id of the guild the even got fired on
 * @property encodedTrack the base64 encoded track
 */
public interface TrackEvent : Event {
    public val encodedTrack: String

    /**
     * Converts [encodedTrack] into a [Track].
     */
    public suspend fun getTrack(): Track = Track.fromLavalink(encodedTrack)
}

/**
 * Even fired when a new track starts playing.
 *
 * @see TrackEvent
 */
public interface TrackStartEvent : TrackEvent

/**
 * Track fired when a track ended.
 *
 * @property reason the [EndReason] for this track ending
 * @see TrackEvent
 */
public interface TrackEndEvent : TrackEvent {
    public val reason: EndReason

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
}

/**
 * Event fired when an [Exception] occurs whilst trying to play a track.
 *
 * @property exception the [Exception] thrown
 * @see TrackEvent
 */
public interface TrackExceptionEvent : TrackEvent {
    public val exception: Exception
}

/**
 * Event that is fired when a track was started, but no audio frames from it have arrived in a ULong time
 *
 * @property threshold The wait threshold that was exceeded for this event to trigger
 * @see TrackEvent
 */
public interface TrackStuckEvent : TrackEvent {
    public val threshold: Duration
}

/**
 * Event fired when Discord closes the websocket connection.
 *
 * @property code the [Discord Websocket close code](https://discord.com/developers/docs/topics/opcodes-and-status-codes#voice-voice-close-event-codes)
 * @property reason the detailed reason why the connection was closed
 * @property byRemote whether the connection was closed by the remote or not
 */
public interface WebSocketClosedEvent : Event {
    public val code: Int
    public val reason: String
    public val byRemote: Boolean
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

/**
 * Listens for a [TrackEvent]
 *
 * @see defaultOn
 */
public inline fun <reified T : TrackEvent> EventSource<TrackEvent>.on(
    scope: CoroutineScope = coroutineScope,
    noinline consumer: suspend T.() -> Unit
): Job = defaultOn<TrackEvent, T>(scope, consumer)
