package dev.schlaubi.lavakord.audio.player

import dev.schlaubi.lavakord.audio.Event
import dev.schlaubi.lavakord.audio.EventSource
import dev.schlaubi.lavakord.rest.models.PartialTrack
import dev.schlaubi.lavakord.rest.loadItem
import dev.schlaubi.lavakord.rest.models.TrackResponse
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * Interface allowing interaction with Lavalink player.
 *
 * @property playingTrack the currently playing [Track]
 * @property paused whether the playback is currently paused or not
 * @property volume the current volume of this player
 * @property position the position of the current song the player is at (-1 if [playingTrack] is null)
 * @property positionDuration the position of the current song the player is at (-1 if [playingTrack] is null)
 * @property equalizers the applied equalizers in this player
 */
public interface Player : EventSource<Event> {
    public val playingTrack: Track?
    public val paused: Boolean
    public val volume: Int
    public val positionDuration: Duration
    public val position: Long
        get() = positionDuration.inWholeMilliseconds

    public val equalizers: Map<Int, Float>

    /**
     * Changes the currently playing track to [track].
     */
    public suspend fun playTrack(track: Track, playOptionsBuilder: PlayOptions.() -> Unit = {}): Unit =
        playTrack(track.track, playOptionsBuilder)

    /**
     * Changes the currently playing track to [track].
     */
    public suspend fun playTrack(
        track: PartialTrack,
        playOptionsBuilder: PlayOptions.() -> Unit = {}
    ): Unit = playTrack(track.encoded, playOptionsBuilder)

    /**
     * Changes the currently playing track to [track].
     *
     * @param track the lavalink encoded track
     */
    public suspend fun playTrack(track: String, playOptionsBuilder: PlayOptions.() -> Unit = {})

    /**
     * Directly plays a single track.
     *
     * **Important:** This only works if [loadItem] would return with [TrackResponse.LoadType.TRACK_LOADED], for search
     * and playlists use [loadItem]
     */
    public suspend fun searchAndPlayTrack(identifier: String, playOptionsBuilder: PlayOptions.() -> Unit)

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
    public suspend fun seekTo(position: Duration): Unit = seekTo(position.toLong(DurationUnit.MILLISECONDS))

    /**
     * Seeks to a specific [position] in the current playback.
     *
     * @param position the position in the track in milliseconds
     */
    public suspend fun seekTo(position: Long)

    /**
     * The current [Filters] settings.
     */
    public val filters: Filters
}
