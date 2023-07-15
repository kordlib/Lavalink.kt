@file:Suppress("KDocMissingDocumentation")

package dev.schlaubi.lavakord.rest.routes

import dev.schlaubi.lavakord.PluginApi
import dev.schlaubi.lavakord.rest.routes.V4Api.Sessions.Specific
import io.ktor.resources.*

@Resource("version")
@PluginApi
public class Version

@Resource("/v4")
@PluginApi
public class V4Api {

    @Resource("websocket")
    public data class WebSocket(val api: V4Api = V4Api())

    @Resource("sessions")
    public data class Sessions(val api: V4Api = V4Api()) {

        @Resource("{sessionId}")
        public data class Specific(val sessionId: String, val sessions: Sessions = Sessions()) {
            @Resource("players")
            public data class Players(val specific: Sessions.Specific) {
                public constructor(sessionId: String) : this(Specific(sessionId))

                @Resource("{guildId}")
                public data class Specific(val guildId: ULong, val noReplace: Boolean? = null, val players: Players) {
                    public constructor(guildId: ULong, sessionId: String, noReplace: Boolean? = false) : this(
                        guildId,
                        noReplace,
                        Players(sessionId)
                    )
                }
            }
        }
    }

    @Resource("loadtracks")
    public data class LoadTracks(val identifier: String, val api: V4Api = V4Api())

    @Resource("decodetrack")
    public data class DecodeTrack(val encodedTrack: String? = null, val api: V4Api = V4Api())

    @Resource("info")
    public data class Info(val api: V4Api = V4Api())

    @Resource("stats")
    public data class Stats(val stats: V4Api = V4Api())

    @Resource("routeplanner")
    public data class RoutePlanner(val api: V4Api = V4Api()) {
        @Resource("status")
        public data class Status(val routePlanner: RoutePlanner = RoutePlanner())

        @Resource("free")
        public data class Free(val routePlanner: RoutePlanner = RoutePlanner()) {

            @Resource("address")
            public data class Address(val free: Free = Free())

            @Resource("all")
            public data class All(val free: Free = Free())
        }
    }
}
