package me.schlaubi.lavakord.audio.player

import me.schlaubi.lavakord.audio.EventSource
import me.schlaubi.lavakord.audio.Track
import me.schlaubi.lavakord.audio.TrackEvent
import me.schlaubi.lavakord.rest.TrackResponse
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Interface allowing interaction with Lavalink player.
 *
 * @property playingTrack the currently playing [Track]
 * @property paused whether the playback is currently paused or not
 * @property volume the current volume of this player
 */
public interface Player : EventSource<TrackEvent> {
    public val playingTrack: Track?
    public val paused: Boolean
    public val volume: Int
    public val position: Long

    /**
     * Changes the currently playing track to [track].
     */
    public suspend fun playTrack(track: Track): Unit = playTrack(track.track)

    /**
     * Changes the currently playing track to [track].
     */
    public suspend fun playTrack(track: TrackResponse.PartialTrack): Unit = playTrack(track.track)

    /**
     * Changes the currently playing track to [track].
     *
     * @param track the lavalink encoded track
     */
    public suspend fun playTrack(track: String)

    /**
     * Stops playback of the currently playing track.
     */
    public suspend fun stopTrack()

    /**
     * Pauses/unpauses playback of the current track.
     *
     * @param doPause whether the playback should be paused or un-paused
     */
    public suspend fun pause(doPause: Boolean = true)

    /**
     * Unpauses the playback
     *
     * @see pause
     */
    public suspend fun unPause(): Unit = pause(false)

    /**
     * Seeks to a specific [position] in the current playback.
     */
    @OptIn(ExperimentalTime::class)
    public suspend fun seekTo(position: Duration): Unit = seekTo(position.inMilliseconds.toLong())

    /**
     * Seeks to a specific [position] in the current playback.
     *
     * @param position the position in the track in milliseconds
     */
    public suspend fun seekTo(position: Long)

    /**
     * Changes the volume of the current player.
     */
    public suspend fun setVolume(volume: Int)
}
