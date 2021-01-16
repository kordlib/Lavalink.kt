package rest

import AUTH_HEADER
import RestTestLavakord
import TestNode
import Tests.runBlocking
import dev.kord.x.lavalink.audio.internal.AbstractLavakord
import dev.kord.x.lavalink.rest.TrackResponse
import dev.kord.x.lavalink.rest.loadItem
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.JsonFeature.Feature.install
import io.ktor.http.*
import json.invoke
import json.neverGonnaGiveYouUp
import json.respondJson
import json.shouldBe
import json.src.*
import mu.KotlinLogging
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private val LOG = KotlinLogging.logger {  }

private const val SINGLE_SONG = "single_song"
private const val PLAYLIST = "playlist"
private const val SEARCH = "search"
private const val NOTHING = "random_jibberish"
private const val ERROR_ON_LOAD = "ErRoR"

class TrackLoadingTest {

    private val lavakord: AbstractLavakord = RestTestLavakord(TrackLoadingMock)
    private val node = TestNode(lavakord)

    @JsName("requestSingleSong")
    @Test
    fun `request single song`() {
        requestTrack(SINGLE_SONG) {
            loadType shouldBe TrackResponse.LoadType.TRACK_LOADED
            track shouldBe neverGonnaGiveYouUp
        }
    }

    @JsName("requestPlaylistSong")
    @Test
    fun `request playlist`() {
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

    @JsName("requestSearch")
    @Test
    fun `request search`() {
        requestTrack(SEARCH) {
            loadType shouldBe TrackResponse.LoadType.SEARCH_RESULT
            tracks shouldBe listOf(neverGonnaGiveYouUp, neverGonnaGiveYouUp, neverGonnaGiveYouUp)
            assertFailsWith<IllegalStateException> { getPlaylistInfo() }
        }
    }

    @JsName("requestNothing")
    @Test
    fun `request nothing`() {
        requestTrack(NOTHING) {
            loadType shouldBe TrackResponse.LoadType.NO_MATCHES
            assertTrue(tracks.isEmpty())
        }
    }

    @JsName("requestError")
    @Test
    fun `request error`() {
        requestTrack(ERROR_ON_LOAD) {
            loadType shouldBe TrackResponse.LoadType.LOAD_FAILED
            val exception = getException()
            exception {
                message shouldBe "The uploader has not made this video available in your country."
                severity shouldBe TrackResponse.Error.Severity.COMMON
            }
        }
    }

    private fun requestTrack(input: String, checker: TrackResponse.() -> Unit) {
        println("REQ start")
        Tests.runBlocking {
            node.loadItem(input).run {
                println("Received: $this")
                checker(this)
            }
        }
    }
}

object TrackLoadingMock : HttpClientEngineFactory<HttpClientEngineConfig> {

    override fun create(block: HttpClientEngineConfig.() -> Unit): HttpClientEngine {
        val configure: MockEngineConfig.() -> Unit = {
            loadItem()
            block(this)
        }
        return MockEngine.create(configure)
    }

    fun MockEngineConfig.loadItem() {
        addHandler { request ->
            if (request.headers["Authorization"] != AUTH_HEADER) {
                respond("Unauthorized", HttpStatusCode.Unauthorized)
            } else if (request.url.fullPath.substringAfter('/').substringBefore('?') == "loadtracks") {
                val identifier = request.url.parameters["identifier"]
                    ?: return@addHandler respond("Bad Request (missing identifier)", HttpStatusCode.BadRequest)

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
