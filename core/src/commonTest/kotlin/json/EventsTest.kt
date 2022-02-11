package json

import dev.schlaubi.lavakord.audio.internal.GatewayPayload
import json.src.*
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertNotNull

class EventsTest {

    @JsName("testPlayerUpdateEvent")
    @Test
    fun `test player update event serialization`() {
        testPayload<GatewayPayload.PlayerUpdateEvent>(PLAYER_UPDATE_EVENT) {
            state shouldBe GatewayPayload.PlayerUpdateEvent.State(1500467109, 1500467109, true)
        }
    }

    @JsName("testStatsEvent")
    @Test
    fun `test stats event serialization`() {
        fun GatewayPayload.StatsEvent.validateBasic() {
            players shouldBe 1
            playingPlayers shouldBe 1
            memory {
                free shouldBe 100
                used shouldBe 100
                allocated shouldBe 100
                reservable shouldBe 100
            }
            cpu {
                cores shouldBe 10
                systemLoad shouldBe .50
                lavalinkLoad shouldBe .10
            }
        }

        testPayload<GatewayPayload.StatsEvent>(FULL_STATS_EVENT) {
            validateBasic()
            frameStats {
                assertNotNull(this)

                sent shouldBe 10
                nulled shouldBe 10
                deficit shouldBe 10
            }
        }

        testPayload<GatewayPayload.StatsEvent>(STATS_EVENT) {
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

        testPayload<GatewayPayload.EmittedEvent>(TRACK_START_EVENT) {
            validateBasic("TrackStartEvent")
        }

        testPayload<GatewayPayload.EmittedEvent>(TRACK_END_EVENT) {
            validateBasic("TrackEndEvent")
            reason shouldBe "FINISHED"
        }

        testPayload<GatewayPayload.EmittedEvent>(TRACK_EXCEPTION_EVENT) {
            validateBasic("TrackExceptionEvent")
            error shouldBe "An error occurred"
        }

        testPayload<GatewayPayload.EmittedEvent>(TRACK_STUCK_EVENT) {
            validateBasic("TrackStuckEvent")
            thresholdMs shouldBe 500
        }

        testPayload<GatewayPayload.EmittedEvent>(WEBSOCKET_CLOSED_EVENT) {
            validateBasic("WebSocketClosedEvent")
            code shouldBe 4006
            reason shouldBe "Your session is no longer valid."
            byRemote shouldBe true
        }
    }
}
