package dev.schlaubi.lavakord.audio.internal

import dev.schlaubi.lavakord.audio.*
import dev.schlaubi.lavakord.audio.player.*
import dev.schlaubi.lavakord.rest.destroyPlayer
import dev.schlaubi.lavakord.rest.models.FiltersObject
import dev.schlaubi.lavakord.rest.models.UpdatePlayerRequest
import dev.schlaubi.lavakord.rest.updatePlayer
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

    override val volume: Int
        get() = ((filters.volume ?: 1.0f) * 100).toInt()

    @Suppress("unused")
    override var filters: Filters = FiltersObject()
        internal set

    override val equalizers: Map<Int, Float>
        get() = filters.equalizers
            .associateBy(Equalizer::band)
            .mapValues { (_, band) ->
                band.gain
            }

    override val events: Flow<Event>
        get() = node.events.filter { it.guildId == guildId }

    init {
        on(consumer = ::handleNewTrack)
        on(consumer = ::handleTrackEnd)
    }

    override suspend fun playTrack(track: String, playOptionsBuilder: PlayOptions.() -> Unit) =
        playTrackInternal(track = track, playOptionsBuilder = playOptionsBuilder)

    override suspend fun searchAndPlayTrack(identifier: String, playOptionsBuilder: PlayOptions.() -> Unit) =
        playTrackInternal(identifier = identifier, playOptionsBuilder = playOptionsBuilder)

    private suspend fun playTrackInternal(
        track: String? = null,
        identifier: String? = null,
        playOptionsBuilder: PlayOptions.() -> Unit
    ) {
        val options = PlayOptions().apply(playOptionsBuilder)
        node.updatePlayer(
            guildId, options.noReplace, UpdatePlayerRequest(
                encodedTrack = track,
                identifier = identifier,
                position = options.position?.inWholeMilliseconds,
                endTime = options.end?.inWholeMilliseconds,
                volume = options.volume,
                paused = options.pause,
                filters = options.filters
            )
        )
    }

    private suspend fun handleNewTrack(event: TrackStartEvent) {
        updateTime = Clock.System.now()
        val track = event.getTrack()
        lastPosition = track.position
        playingTrack = track
    }

    private fun handleTrackEnd(@Suppress("UNUSED_PARAMETER") event: TrackEndEvent) {
        playingTrack = null
        lastPosition = 0.milliseconds
    }

    override suspend fun stopTrack() {
        node.destroyPlayer(guildId)
        playingTrack = null
    }

    override suspend fun pause(doPause: Boolean) {
        if (paused == doPause) return
        node.updatePlayer(guildId, request = UpdatePlayerRequest(paused = doPause))
        paused = doPause
    }

    override suspend fun seekTo(position: Long) {
        checkNotNull(playingTrack) { "Not currently playing anything" }
        check(playingTrack?.isSeekable == true) { "Current track is not seekable" }

        node.updatePlayer(guildId, request = UpdatePlayerRequest(position = position))
    }

    internal fun provideState(state: GatewayPayload.PlayerUpdateEvent.State) {
        updateTime = Instant.fromEpochMilliseconds(state.time)
        lastPosition = state.position?.milliseconds ?: 0.milliseconds
    }
}
