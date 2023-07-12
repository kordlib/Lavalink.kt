package dev.schlaubi.lavakord.audio.internal

import dev.arbjerg.lavalink.protocol.v4.*
import dev.schlaubi.lavakord.audio.*
import kotlin.time.Duration

internal fun Message.EmittedEvent.toEvent(): Event = when (this) {
    is Message.EmittedEvent.TrackEndEvent -> TrackEndEvent(this)
    is Message.EmittedEvent.TrackExceptionEvent -> TrackExceptionEvent(this)
    is Message.EmittedEvent.TrackStartEvent -> TrackStartEvent(this)
    is Message.EmittedEvent.TrackStuckEvent -> TrackStuckEvent(this)
    is Message.EmittedEvent.WebSocketClosedEvent -> WebSocketClosedEvent(this)
}

private fun TrackEndEvent(delegate: Message.EmittedEvent.TrackEndEvent) = object : TrackEndEvent {
    override val reason: Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason
        get() = delegate.reason
    override val track: Track
        get() = delegate.track
    override val guildId: ULong
        get() = delegate.guildId.toULong()

}

private fun TrackExceptionEvent(delegate: Message.EmittedEvent.TrackExceptionEvent) = object : TrackExceptionEvent {
    override val exception: Exception
        get() = delegate.exception
    override val track: Track
        get() = delegate.track
    override val guildId: ULong
        get() = delegate.guildId.toULong()

}

private fun TrackStartEvent(delegate: Message.EmittedEvent.TrackStartEvent) = object : TrackStartEvent {
    override val track: Track
        get() = delegate.track
    override val guildId: ULong
        get() = delegate.guildId.toULong()

}

private fun TrackStuckEvent(delegate: Message.EmittedEvent.TrackStuckEvent) = object : TrackStuckEvent {
    override val threshold: Duration
        get() = delegate.threshold
    override val track: Track
        get() = delegate.track
    override val guildId: ULong
        get() = delegate.guildId.toULong()

}

private fun WebSocketClosedEvent(delegate: Message.EmittedEvent.WebSocketClosedEvent) = object : WebSocketClosedEvent {
    override val code: Int
        get() = delegate.code
    override val reason: String
        get() = delegate.reason
    override val byRemote: Boolean
        get() = delegate.byRemote
    override val guildId: ULong
        get() = delegate.guildId.toULong()

}
