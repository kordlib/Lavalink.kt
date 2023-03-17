package json

import dev.schlaubi.lavakord.Exception
import dev.schlaubi.lavakord.rest.models.PartialTrack
import dev.schlaubi.lavakord.rest.models.TrackResponse
import json.src.*
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertTrue

val neverGonnaGiveYouUp = PartialTrack(
    "QAAAjQIAJVJpY2sgQXN0bGV5IC0gTmV2ZXIgR29ubmEgR2l2ZSBZb3UgVXAADlJpY2tBc3RsZXlWRVZPAAAAAAADPCAAC2RRdzR3OVdnWGNRAAEAK2h0dHBzOi8vd3d3LnlvdXR1YmUuY29tL3dhdGNoP3Y9ZFF3NHc5V2dYY1EAB3lvdXR1YmUAAAAAAAAAAA==",
    PartialTrack.Info(
        "dQw4w9WgXcQ",
        true,
        "RickAstleyVEVO",
        212000,
        false,
        0,
        "Rick Astley - Never Gonna Give You Up",
        "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
        "youtube"
    )
)

class TrackRestTest {


    @JsName("testSingleTrack")
    @Test
    fun `test single track loaded`() {
        test<TrackResponse>(TRACK_LOADED) {
            loadType shouldBe TrackResponse.LoadType.TRACK_LOADED
            tracks shouldBe listOf(neverGonnaGiveYouUp)
        }
    }

    @JsName("testPlaylistAndSearchResult")
    @Test
    fun `test playlist and searchResult loaded`() {
        fun TrackResponse.validateTracks() {
            tracks shouldBe listOf(neverGonnaGiveYouUp, neverGonnaGiveYouUp, neverGonnaGiveYouUp)
        }

        test<TrackResponse>(SEARCH_RESULT) {
            loadType shouldBe TrackResponse.LoadType.SEARCH_RESULT
            validateTracks()
        }

        test<TrackResponse>(PLAYLIST_LOADED) {
            loadType shouldBe TrackResponse.LoadType.PLAYLIST_LOADED
            validateTracks()
            val playlistInfo = getPlaylistInfo()
            playlistInfo {
                name shouldBe "Example YouTube Playlist"
                selectedTrack shouldBe 3
            }
        }
    }

    @JsName("testNoResults")
    @Test
    fun `test no results found`() {
        test<TrackResponse>(NO_MATCHES) {
            loadType shouldBe TrackResponse.LoadType.NO_MATCHES
            assertTrue(tracks.isEmpty())
        }
    }

    @JsName("testLoadFailed")
    @Test
    fun `test load failed`() {
        test<TrackResponse>(LOAD_FAILED) {
            loadType shouldBe TrackResponse.LoadType.LOAD_FAILED
            assertTrue(tracks.isEmpty())
            val exception = getException()
            exception {
                message shouldBe "The uploader has not made this video available in your country."
                severity shouldBe Exception.Severity.COMMON
            }
        }
    }
}
