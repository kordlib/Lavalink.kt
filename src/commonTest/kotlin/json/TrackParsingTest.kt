package json

import dev.kord.x.lavalink.audio.player.Track
import json.src.TRACK
import kotlinx.coroutines.launch
import kotlin.test.Test

class TrackParsingTest {
    @Test
    fun `NEVVA GONNA GIVE YOU UP`() {
        Tests.launch {
            Track.fromLavalink(TRACK).run {
                track shouldBe TRACK
                identifier shouldBe "dQw4w9WgXcQ"
                isSeekable shouldBe false
                author shouldBe "RickAstleyVEVO"
                length shouldBe 212000
                isStream shouldBe false
                position shouldBe 0
                title shouldBe "Rick Astley - Never Gonna Give You Up"
                uri shouldBe "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
            }
        }
    }
}
