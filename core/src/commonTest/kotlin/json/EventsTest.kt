package json

import dev.schlaubi.lavakord.Exception
import dev.schlaubi.lavakord.audio.*
import dev.schlaubi.lavakord.audio.internal.GatewayPayload
import json.src.*
import kotlin.contracts.ExperimentalContracts
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.milliseconds

class EventsTest {

    @JsName("testPlayerUpdateEvent")
    @Test
    fun `test player update event serialization`() {
        testPayload<GatewayPayload.PlayerUpdateEvent>(PLAYER_UPDATE_EVENT) {
            state shouldBe GatewayPayload.PlayerUpdateEvent.State(1500467109, 1500467109, true, 1337)
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

    @OptIn(ExperimentalContracts::class)
    private inline fun <reified T : Event> GatewayPayload.EmittedEvent.validateBasic(validator: T.() -> Unit = {}) {
        if (this is TrackEvent) {
            encodedTrack shouldBe TRACK
        }
        type shouldBe type
        validator(this as T)
    }

    @JsName("testEmittedEvent")
    @Test
    fun `test emitted event serialization`() {

        testPayload<GatewayPayload.EmittedEvent>(TRACK_START_EVENT) {
            validateBasic<TrackStartEvent>()
        }

        testPayload<GatewayPayload.EmittedEvent>(TRACK_END_EVENT) {
            validateBasic<TrackEndEvent> {
                reason shouldBe TrackEndEvent.EndReason.FINISHED
            }
        }

        testPayload<GatewayPayload.EmittedEvent>(TRACK_EXCEPTION_EVENT) {
            validateBasic<TrackExceptionEvent> {
                exception {
                    message shouldBe "..."
                    severity shouldBe Exception.Severity.COMMON
                    cause shouldBe "..."
                }
            }
        }

        testPayload<GatewayPayload.EmittedEvent>(TRACK_STUCK_EVENT) {
            validateBasic<TrackStuckEvent> {
                threshold shouldBe 500.milliseconds
            }
        }

        testPayload<GatewayPayload.EmittedEvent>(WEBSOCKET_CLOSED_EVENT) {
            validateBasic<WebSocketClosedEvent> {
                code shouldBe 4006
                reason shouldBe "Your session is no longer valid."
                byRemote shouldBe true
            }
        }
    }
}
