package rest

import RestTestLavakord
import TestNode
import Tests
import Tests.runBlocking
import dev.schlaubi.lavakord.audio.internal.AbstractLavakord
import dev.schlaubi.lavakord.rest.TrackResponse
import dev.schlaubi.lavakord.rest.loadItem
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.http.*
import json.invoke
import json.neverGonnaGiveYouUp
import json.respondJson
import json.shouldBe
import json.src.*
//import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private const val SINGLE_SONG = "single_song"
private const val PLAYLIST = "playlist"
private const val SEARCH = "search"
private const val NOTHING = "random_jibberish"
private const val ERROR_ON_LOAD = "ErRoR"

class TrackLoadingTest {

    private val mockEngine = RestHttpEngine {
        loadItem()
    }

    private val lavakord: AbstractLavakord = RestTestLavakord(mockEngine)
    private val node = TestNode(lavakord)

    //@JsName("requestSingleSong")
    @Test
    fun `test loading a single track`() {
        requestTrack(SINGLE_SONG) {
            loadType shouldBe TrackResponse.LoadType.TRACK_LOADED
            track shouldBe neverGonnaGiveYouUp
        }
    }

    //@JsName("requestPlaylistSong")
    @Test
    fun `test loading a playlist`() {
        requestTrack(PLAYLIST) {
            loadType shouldBe TrackResponse.LoadType.PLAYLIST_LOADED
            tracks shouldBe listOf(neverGonnaGiveYouUp, neverGonnaGiveYouUp, neverGonnaGiveYouUp)
            val playlistInfo = getPlaylistInfo()
            playlistInfo {
                name shouldBe "Example YouTube Playlist"
                selectedTrack shouldBe 3
            }
        }
    }

    //@JsName("requestSearch")
    @Test
    fun `test searching a track`() {
        requestTrack(SEARCH) {
            loadType shouldBe TrackResponse.LoadType.SEARCH_RESULT
            testTrack()
            tracks shouldBe listOf(neverGonnaGiveYouUp, neverGonnaGiveYouUp, neverGonnaGiveYouUp)
            assertFailsWith<IllegalStateException> { track }
            assertFailsWith<IllegalStateException> { getPlaylistInfo() }
        }
    }

    //@JsName("requestNothing")
    @Test
    fun `test requesting nothing`() {
        requestTrack(NOTHING) {
            loadType shouldBe TrackResponse.LoadType.NO_MATCHES
            testTrack()
            assertTrue(tracks.isEmpty())
        }
    }

    //@JsName("requestError")
    @Test
    fun `test handling of error on lavalink node`() {
        requestTrack(ERROR_ON_LOAD) {
            loadType shouldBe TrackResponse.LoadType.LOAD_FAILED
            testTrack()
            val exception = getException()
            exception {
                message shouldBe "The uploader has not made this video available in your country."
                severity shouldBe TrackResponse.Error.Severity.COMMON
            }
        }
    }

    private fun requestTrack(input: String, checker: TrackResponse.() -> Unit) {
        Tests.runBlocking {
            node.loadItem(input).run {
                checker(this)
            }
        }
    }

    private fun TrackResponse.testTrack() =
        assertFailsWith<IllegalStateException>("Track has to throw exception on not single track results") { track }
}

private fun MockEngineConfig.loadItem() {
    addHandler { request ->
        checkAuth(request) {
            if (request.url.fullPath.substringAfter('/').substringBefore('?') == "loadtracks") {
                val identifier = request.url.parameters["identifier"]
                    ?: return@checkAuth respond("Bad Request (missing identifier)", HttpStatusCode.BadRequest)

                val response = when (identifier) {
                    SINGLE_SONG -> TRACK_LOADED
                    PLAYLIST -> PLAYLIST_LOADED
                    SEARCH -> SEARCH_RESULT
                    NOTHING -> NO_MATCHES
                    ERROR_ON_LOAD -> LOAD_FAILED
                    else -> error("Unknown identifier")
                }

                respondJson(response)
            } else error("Unknown request path ${request.url.fullPath}")
        }
    }
}
