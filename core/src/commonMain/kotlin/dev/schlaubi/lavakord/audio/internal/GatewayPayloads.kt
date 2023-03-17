@file:Suppress("unused")

package dev.schlaubi.lavakord.audio.internal

import dev.schlaubi.lavakord.Exception
import dev.schlaubi.lavakord.audio.Event
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import dev.schlaubi.lavakord.audio.StatsEvent as PublicStatsEvent
import dev.schlaubi.lavakord.audio.TrackEndEvent as PublicTrackEndEvent
import dev.schlaubi.lavakord.audio.TrackExceptionEvent as PublicTrackExceptionEvent
import dev.schlaubi.lavakord.audio.TrackStartEvent as PublicTrackStartEvent
import dev.schlaubi.lavakord.audio.TrackStuckEvent as PublicTrackStuckEvent
import dev.schlaubi.lavakord.audio.WebSocketClosedEvent as PublicWebsocketClosedEvent

internal interface SanitizablePayload<T : GatewayPayload> {
    fun sanitize(): T
}

// Workaround for weird kx.ser bug
internal val GatewayModule = SerializersModule {
    contextual(GatewayPayload::class, GatewayPayload.serializer())
}

@Serializable(with = GatewayPayload.Serializer::class)
internal sealed class GatewayPayload {
    abstract val op: String

    interface GuildAware {
        val guildId: ULong
    }

    @Serializable
    data class ReadyEvent(
        override val op: String,
        val resumed: Boolean,
        val sessionId: String
    ) : GatewayPayload()

    @Serializable
    data class PlayerUpdateEvent(
        override val op: String,
        override val guildId: ULong, val state: State
    ) : GatewayPayload(), GuildAware {
        @Serializable
        data class State(val time: Long, val position: Long? = null, val connected: Boolean, val ping: Int)
    }

    @Serializable
    data class StatsEvent(
        override val op: String,
        override val players: Int,
        override val playingPlayers: Int,
        override val uptime: Long,
        override val memory: PublicStatsEvent.Memory,
        override val cpu: PublicStatsEvent.Cpu,
        override val frameStats: PublicStatsEvent.FrameStats? = null
    ) : GatewayPayload(), PublicStatsEvent

    @Serializable(with = EmittedEvent.Serializer::class)
    sealed class EmittedEvent : GatewayPayload(), GuildAware, Event {
        abstract val type: Type

        @Serializable
        enum class Type {
            @SerialName("TrackStartEvent")
            TRACK_START_EVENT,

            @SerialName("TrackEndEvent")
            TRACK_END_EVENT,

            @SerialName("TrackExceptionEvent")
            TRACK_EXCEPTION_EVENT,

            @SerialName("TrackStuckEvent")
            TRACK_STUCK_EVENT,

            @SerialName("WebSocketClosedEvent")
            WEBSOCKET_CLOSED_EVENT
        }

        @Serializable
        data class TrackStartEvent(
            override val op: String,
            override val guildId: ULong,
            override val encodedTrack: String,
            override val type: Type
        ) : EmittedEvent(), PublicTrackStartEvent

        @Serializable
        data class TrackEndEvent(
            override val op: String,
            override val guildId: ULong,
            override val encodedTrack: String,
            override val reason: PublicTrackEndEvent.EndReason,
            override val type: Type
        ) : EmittedEvent(), PublicTrackEndEvent

        @Serializable
        data class TrackExceptionEvent(
            override val op: String,
            override val guildId: ULong,
            override val encodedTrack: String,
            override val exception: Exception,
            override val type: Type
        ) : EmittedEvent(), PublicTrackExceptionEvent

        @Serializable
        data class TrackStuckEvent(
            override val op: String,
            override val guildId: ULong,
            override val encodedTrack: String,
            val thresholdMs: Int,
            override val type: Type
        ) : EmittedEvent(), PublicTrackStuckEvent {
            override val threshold: Duration by lazy { thresholdMs.toDuration(DurationUnit.MILLISECONDS) }
        }

        @Serializable
        data class WebSocketClosedEvent(
            override val op: String,
            override val guildId: ULong,
            override val code: Int,
            override val reason: String,
            override val byRemote: Boolean,
            override val type: Type
        ) : EmittedEvent(), PublicWebsocketClosedEvent

        companion object Serializer : JsonContentPolymorphicSerializer<EmittedEvent>(EmittedEvent::class) {
            override fun selectDeserializer(element: JsonElement): DeserializationStrategy<EmittedEvent> {
                val type = element.jsonObject["type"] ?: error("Missing type")
                return when (Json.decodeFromJsonElement<Type>(type)) {
                    Type.TRACK_START_EVENT -> TrackStartEvent.serializer()
                    Type.TRACK_END_EVENT -> TrackEndEvent.serializer()
                    Type.TRACK_EXCEPTION_EVENT -> TrackExceptionEvent.serializer()
                    Type.TRACK_STUCK_EVENT -> TrackStuckEvent.serializer()
                    Type.WEBSOCKET_CLOSED_EVENT -> WebSocketClosedEvent.serializer()
                }
            }
        }
    }

    companion object Serializer : JsonContentPolymorphicSerializer<GatewayPayload>(GatewayPayload::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<GatewayPayload> =
            when (val event = element.jsonObject["op"]!!.jsonPrimitive.content) {
                "ready" -> ReadyEvent.serializer()
                "stats" -> StatsEvent.serializer()
                "playerUpdate" -> PlayerUpdateEvent.serializer()
                "event" -> EmittedEvent.serializer()
                else -> error("Unknown event: $event")
            }
    }
}
