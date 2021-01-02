package me.schlaubi.lavakord.audio.impl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import me.schlaubi.lavakord.audio.*
import me.schlaubi.lavakord.audio.GatewayPayload
import me.schlaubi.lavakord.audio.player.Player

internal class WebsocketPlayer(internal val node: NodeImpl, internal val guildId: Long) : Player {
    override var playingTrack: Track? = null
    override val coroutineScope: CoroutineScope
        get() = node.coroutineScope
    override var paused: Boolean = false
    override var position: Long = 0
    private var updateTime: Long = 0
    override var volume: Int = 100

    @Suppress("unused")
    internal var filters: GatewayPayload.FiltersCommand = GatewayPayload.FiltersCommand(guildId.toString())

    @Suppress("unused")
    internal var equalizerBuilder: EqualizerBuilder = EqualizerBuilder(guildId)

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
        playingTrack = event.track
    }

    private fun handleTrackEnd(@Suppress("UNUSED_PARAMETER") event: TrackEndEvent) {
        playingTrack = null
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
        updateTime = state.time
        position = state.position ?: 0
    }
}
