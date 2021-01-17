package json

import Tests.runBlocking
import dev.kord.x.lavalink.audio.player.Track
import json.src.TRACK
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.time.seconds

class TrackParsingTest {
    @JsName("testNeverGonnaGiveYouUp")
    @Test
    fun `NEVVA GONNA GIVE YOU UP`() {
        Tests.runBlocking {
            Track.fromLavalink(TRACK).run {
                track shouldBe TRACK
                identifier shouldBe "dQw4w9WgXcQ"
                isSeekable shouldBe true
                author shouldBe "RickAstleyVEVO"
                length shouldBe 212.seconds
                isStream shouldBe false
                position shouldBe 0.seconds
                title shouldBe "Rick Astley - Never Gonna Give You Up"
                uri shouldBe "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
            }
        }
    }
}
