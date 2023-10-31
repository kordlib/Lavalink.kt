package dev.schlaubi.lavakord.audio.internal

import dev.arbjerg.lavalink.protocol.v4.*
import dev.schlaubi.lavakord.audio.Event
import dev.schlaubi.lavakord.audio.TrackEndEvent
import dev.schlaubi.lavakord.audio.TrackStartEvent
import dev.schlaubi.lavakord.audio.on
import dev.schlaubi.lavakord.audio.player.Equalizer
import dev.schlaubi.lavakord.audio.player.Filters
import dev.schlaubi.lavakord.audio.player.PlayOptions
import dev.schlaubi.lavakord.audio.player.Player
import dev.schlaubi.lavakord.rest.models.FiltersObject
import dev.schlaubi.lavakord.rest.models.toLavalink
import dev.schlaubi.lavakord.rest.updatePlayer
import kotlinx.atomicfu.AtomicBoolean
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

internal class WebsocketPlayer(node: NodeImpl, internal val guildId: ULong) : Player {
    internal var node: NodeImpl = node
        private set
    override var playingTrack: Track? = null
    override val coroutineScope: CoroutineScope
        get() = node.coroutineScope
    override var paused: Boolean = false
    private var lastPosition: Duration = 0.milliseconds
    private var updateTime: Instant = Instant.DISTANT_PAST
    override val positionDuration: Duration
        get() {
            val trackLength = playingTrack?.info?.length?.milliseconds ?: return -1.milliseconds
            val now = Clock.System.now()
            val elapsedSinceUpdate = now - updateTime

            return (lastPosition + elapsedSinceUpdate).coerceAtMost(trackLength)
        }
    private var specifiedEndTime: Duration? = null
    private val isRecreating = atomic(false)

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
            guildId, options.noReplace, PlayerUpdate(
                encodedTrack = track.toOmissible(),
                identifier = identifier.toOmissible(),
                position = options.position?.inWholeMilliseconds.toOmissible(),
                endTime = options.end?.inWholeMilliseconds.toOmissible(),
                volume = options.volume.toOmissible(),
                paused = options.pause.toOmissible(),
                filters = options.filters?.toLavalink().toOmissible()
            )
        )
        specifiedEndTime = options.end
    }

    private fun handleNewTrack(event: TrackStartEvent) {
        updateTime = Clock.System.now()
        val track = event.track
        lastPosition = event.track.info.position.milliseconds
        playingTrack = track
    }

    private fun handleTrackEnd(@Suppress("UNUSED_PARAMETER") event: TrackEndEvent) {
        playingTrack = null
        lastPosition = 0.milliseconds
        specifiedEndTime = null
    }

    override suspend fun stopTrack() {
        node.updatePlayer(
            guildId,
            request = PlayerUpdate(encodedTrack = Omissible(null))
        )
        playingTrack = null
    }

    override suspend fun pause(doPause: Boolean) {
        if (paused == doPause) return
        node.updatePlayer(
            guildId,
            request = PlayerUpdate(paused = doPause.toOmissible())
        )
        paused = doPause
    }

    override suspend fun seekTo(position: Long) {
        checkNotNull(playingTrack) { "Not currently playing anything" }

        node.updatePlayer(
            guildId,
            request = PlayerUpdate(position = position.toOmissible())
        )
    }

    internal fun provideState(state: PlayerState) {
        // After migrating the player to a new node, the new node may send a position of 0 as we are starting a new track.
        // This may cause a race condition where the migrated track starts at close to 0:00 even if the start time should
        // be later. Ignoring the first player update if it is zero fixes this issue.
        if (isRecreating.getAndSet(true) && state.position == 0L) return
        updateTime = Instant.fromEpochMilliseconds(state.time)
        lastPosition = state.position.milliseconds
    }

    internal suspend fun recreatePlayer(node: NodeImpl, voiceState: VoiceState?) {
        this.node = node
        val position = if (playingTrack == null) null else positionDuration.inWholeMilliseconds

        isRecreating.value = true
        node.updatePlayer(
            guildId, noReplace = false, PlayerUpdate(
                encodedTrack = playingTrack?.encoded.toOmissible(),
                identifier = Omissible.omitted(),
                position = position.toOmissible(),
                endTime = specifiedEndTime?.inWholeMilliseconds.toOmissible(),
                volume = volume.toOmissible(),
                paused = paused.toOmissible(),
                filters = filters.toLavalink().toOmissible(),
                voice = voiceState.toOmissible()
            )
        )

        updateTime = Clock.System.now()
        lastPosition = position?.milliseconds ?: 0.milliseconds
    }
}
