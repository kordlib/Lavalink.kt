package dev.schlaubi.lavakord.audio.internal

import dev.schlaubi.lavakord.audio.TrackEndEvent
import dev.schlaubi.lavakord.audio.TrackEvent
import dev.schlaubi.lavakord.audio.TrackStartEvent
import dev.schlaubi.lavakord.audio.on
import dev.schlaubi.lavakord.audio.player.EqualizerBuilder
import dev.schlaubi.lavakord.audio.player.FiltersApi
import dev.schlaubi.lavakord.audio.player.Player
import dev.schlaubi.lavakord.audio.player.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

internal class WebsocketPlayer(internal val node: NodeImpl, internal val guildId: ULong) : Player {
    override var playingTrack: Track? = null
    override val coroutineScope: CoroutineScope
        get() = node.coroutineScope
    override var paused: Boolean = false
    private var lastPosition: Duration = Duration.milliseconds(0)
    private var updateTime: Instant = Instant.DISTANT_PAST
    override val positionDuration: Duration
        get() {
            val now = Clock.System.now()
            val elapsedSinceUpdate = now - updateTime

            return lastPosition + elapsedSinceUpdate
        }

    override var volume: Int = 100

    @FiltersApi
    @Suppress("unused")
    internal var filters: GatewayPayload.FiltersCommand = GatewayPayload.FiltersCommand(guildId.toString())

    @Suppress("unused")
    var equalizerBuilder: EqualizerBuilder = EqualizerBuilder(guildId)

    override val equalizers: Map<Int, Float>
        get() =
            equalizerBuilder.bands
                .associateBy(GatewayPayload.EqualizerCommand.Band::band)
                .mapValues { (_, band) ->
                    band.gain
                }

    override val events: Flow<TrackEvent>
        get() = node.events.filter { it.guildId == guildId }

    init {
        on(consumer = ::handleNewTrack)
        on(consumer = ::handleTrackEnd)
    }

    override suspend fun playTrack(track: String) {
        node.send(
            GatewayPayload.PlayCommand(
                guildId.toString(),
                track,
                volume = volume
            )
        )
    }

    private fun handleNewTrack(event: TrackStartEvent) {
        lastPosition = event.track.position
        playingTrack = event.track
    }

    private fun handleTrackEnd(@Suppress("UNUSED_PARAMETER") event: TrackEndEvent) {
        playingTrack = null
        lastPosition = Duration.milliseconds(0)
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

    override suspend fun setVolume(volume: Int) {
        require(volume > 0) { "Volume can't be negative" }
        require(volume <= 1000) { "Volume can't be greater than 1000" }
        this.volume = volume

        node.send(GatewayPayload.VolumeCommand(guildId.toString(), volume))
    }

    internal fun provideState(state: GatewayPayload.PlayerUpdateEvent.State) {
        updateTime = Instant.fromEpochMilliseconds(state.time)
        lastPosition = state.position?.let { Duration.milliseconds(it) } ?: Duration.milliseconds(0)
    }
}
