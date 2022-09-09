package dev.schlaubi.lavakord.audio.internal

import dev.schlaubi.lavakord.audio.TrackEndEvent
import dev.schlaubi.lavakord.audio.TrackEvent
import dev.schlaubi.lavakord.audio.TrackStartEvent
import dev.schlaubi.lavakord.audio.on
import dev.schlaubi.lavakord.audio.player.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class WebsocketPlayer(internal val node: NodeImpl, internal val guildId: ULong) : Player {
    override var playingTrack: Track? = null
    override val coroutineScope: CoroutineScope
        get() = node.coroutineScope
    override var paused: Boolean = false
    private var lastPosition: Duration = 0.milliseconds
    private var updateTime: Instant = Instant.DISTANT_PAST
    override val positionDuration: Duration
        get() {
            val now = Clock.System.now()
            val elapsedSinceUpdate = now - updateTime

            return lastPosition + elapsedSinceUpdate
        }

    override var volume: Int
        @Deprecated("Please use the new filters system to specify volume")
        set(value) {
            filters.volume = value / 100f
        }
        get() = ((filters.volume ?: 1.0f) * 100).toInt()

    @Suppress("unused")
    override var filters: Filters = GatewayPayload.FiltersCommand(guildId.toString())
        internal set

    override val equalizers: Map<Int, Float>
        get() = filters.bands
            .associateBy(Band::band)
            .mapValues { (_, band) ->
                band.gain
            }

    override val events: Flow<TrackEvent>
        get() = node.events.filter { it.guildId == guildId }

    init {
        on(consumer = ::handleNewTrack)
        on(consumer = ::handleTrackEnd)
    }

    override suspend fun playTrack(track: String, playOptionsBuilder: PlayOptions.() -> Unit) {
        val options = PlayOptions().apply(playOptionsBuilder)
        node.send(
            GatewayPayload.PlayCommand(
                guildId.toString(),
                track,
                options.startTime,
                options.endTime,
                options.volume,
                options.noReplace,
                options.pause
            )
        )
    }

    private fun handleNewTrack(event: TrackStartEvent) {
        updateTime = Clock.System.now()
        lastPosition = event.track.position
        playingTrack = event.track
    }

    private fun handleTrackEnd(@Suppress("UNUSED_PARAMETER") event: TrackEndEvent) {
        playingTrack = null
        lastPosition = 0.milliseconds
    }

    override suspend fun stopTrack() {
        node.send(GatewayPayload.StopCommand(guildId.toString()))
        playingTrack = null
    }

    override suspend fun pause(doPause: Boolean) {
        if (paused == doPause) return
        node.send(GatewayPayload.PauseCommand(guildId.toString(), doPause))
        paused = doPause
    }

    override suspend fun seekTo(position: Long) {
        checkNotNull(playingTrack) { "Not currently playing anything" }
        check(playingTrack?.isSeekable == true) { "Current track is not seekable" }

        node.send(GatewayPayload.SeekCommand(guildId.toString(), position))
    }

    @Deprecated("Please use the new filters system to specify volume")
    override suspend fun setVolume(volume: Int) {
        require(volume >= 0) { "Volume can't be negative" }
        require(volume <= 500) { "Volume can't be greater than 500" } // Volume <= 5.0

        filters.volume = volume / 100f
        node.send(filters as GatewayPayload.FiltersCommand)
    }

    internal fun provideState(state: GatewayPayload.PlayerUpdateEvent.State) {
        updateTime = Instant.fromEpochMilliseconds(state.time)
        lastPosition = state.position?.milliseconds ?: 0.milliseconds
    }
}
