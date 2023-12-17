@file:Suppress("KDocMissingDocumentation")

package dev.schlaubi.lavakord.plugins.lyrics.rest

import dev.schlaubi.lavakord.rest.routes.V4Api
import io.ktor.resources.*

@Resource("lyrics")
internal data class LyricsRoute(val parent: V4Api = V4Api()) {

    @Resource("lyrics/search/{query}")
    data class Search(val query: String, val parent: LyricsRoute = LyricsRoute())

    @Resource("{videoId}")
    data class Video(val videoId: String, val parent: LyricsRoute = LyricsRoute())

    @Resource("lyrics")
    data class CurrentLyrics(val players: V4Api.Sessions.Specific.Players.Specific) {
        constructor(sessionId: String, guildId: ULong) : this(
            V4Api.Sessions.Specific.Players.Specific(
                guildId,
                sessionId
            )
        )
    }
}
