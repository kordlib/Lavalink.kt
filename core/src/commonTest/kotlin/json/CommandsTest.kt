package json

import dev.schlaubi.lavakord.audio.internal.GatewayPayload
import json.src.*
import kotlin.js.JsName
import kotlin.test.Test

class CommandsTest {

    @JsName("testPlayCommand")
    @Test
    fun `test play command serialization`() {
        testPayload<GatewayPayload.PlayCommand>(PLAY_COMMAND) {
            startTime shouldBe 60000
            endTime shouldBe 120000
            volume shouldBe 100
            noReplace shouldBe false
            pause shouldBe false
        }
    }

    @JsName("testStopCommand")
    @Test
    fun `test stop command serialization`() {
        testPayload<GatewayPayload.StopCommand>(STOP_COMMAND)
    }


    @JsName("testPauseCommand")
    @Test
    fun `test pause command serialization`() {
        testPayload<GatewayPayload.PauseCommand>(PAUSE_COMMAND) {
            pause shouldBe true
        }
    }

    @JsName("testSeekCommand")
    @Test
    fun `test seek command serialization`() {
        testPayload<GatewayPayload.SeekCommand>(SEEK_COMMAND) {
            position shouldBe 60000
        }
    }

    @JsName("testVolumeCommand")
    @Test
    fun `test volume command serialization`() {
        testPayload<GatewayPayload.VolumeCommand>(VOLUME_COMMAND) {
            volume shouldBe 125
        }
    }

    @JsName("testEqCommand")
    @Test
    fun `test equalizer command serialization`() {
        testPayload<GatewayPayload.EqualizerCommand>(EQUALIZER_COMMAND) {
            bands shouldBe listOf(GatewayPayload.EqualizerCommand.Band(0, 0.2F))
        }
    }

    @JsName("testDestroyCommand")
    @Test
    fun `test destroy command serialization`() {
        testPayload<GatewayPayload.DestroyCommand>(DESTROY_COMMAND)
    }
}
