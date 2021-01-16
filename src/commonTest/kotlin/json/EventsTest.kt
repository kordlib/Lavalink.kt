package json

import dev.kord.x.lavalink.audio.StatsEvent
import dev.kord.x.lavalink.audio.internal.GatewayPayload
import json.src.*
import kotlin.js.JsName
import kotlin.test.Test

class EventsTest {

    @JsName("testPlayerUpdateEvent")
    @Test
    fun `test player update event serialization`() {
        test<GatewayPayload.PlayerUpdateEvent>(PLAYER_UPDATE_EVENT) {
            state shouldBe GatewayPayload.PlayerUpdateEvent.State(1500467109, 1500467109)
        }
    }

    @JsName("testStatsEvent")
    @Test
    fun `test stats event serialization`() {
        fun GatewayPayload.StatsEvent.validateBasic() {
            players shouldBe 1
            playingPlayers shouldBe 1
            memory shouldBe StatsEvent.Memory(100, 100, 100, 100)
            cpu shouldBe StatsEvent.Cpu(10, .50, .10)
        }

        test<GatewayPayload.StatsEvent>(FULL_STATS_EVENT) {
            validateBasic()
            frameStats shouldBe StatsEvent.FrameStats(10, 10, 10)
        }

        test<GatewayPayload.StatsEvent>(STATS_EVENT) {
            validateBasic()
            frameStats shouldBe null
        }
    }

    @JsName("testEmittedEvent")
    @Test
    fun `test emitted event serialization`() {
        fun GatewayPayload.EmittedEvent.validateBasic(type: String) {
            if (type != "WebSocketClosedEvent") {
                track shouldBe TRACK
            }
            type shouldBe type
        }

        test<GatewayPayload.EmittedEvent>(TRACK_START_EVENT) {
            validateBasic("TrackStartEvent")
        }

        test<GatewayPayload.EmittedEvent>(TRACK_END_EVENT) {
            validateBasic("TrackEndEvent")
            reason shouldBe "FINISHED"
        }

        test<GatewayPayload.EmittedEvent>(TRACK_EXCEPTION_EVENT) {
            validateBasic("TrackExceptionEvent")
            error shouldBe "An error occurred"
        }

        test<GatewayPayload.EmittedEvent>(TRACK_STUCK_EVENT) {
            validateBasic("TrackStuckEvent")
            thresholdMs shouldBe 500
        }

        test<GatewayPayload.EmittedEvent>(WEBSOCKET_CLOSED_EVENT) {
            validateBasic("WebSocketClosedEvent")
            code shouldBe 4006
            reason shouldBe "Your session is no longer valid."
            byRemote shouldBe true
        }
    }
}
