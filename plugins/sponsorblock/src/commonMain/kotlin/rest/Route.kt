@file:Suppress("KDocMissingDocumentation")

package dev.schlaubi.lavakord.plugins.sponsorblock.rest

import dev.schlaubi.lavakord.rest.routes.V4Api
import io.ktor.resources.*

@Resource("sponsorblock")
public data class SponsorblockRoute(val player: V4Api.Sessions.Specific.Players.Specific) {
    public constructor(guildId: ULong, sessionId: String) : this(
        V4Api.Sessions.Specific.Players.Specific(
            guildId,
            null,
            V4Api.Sessions.Specific.Players(sessionId)
        )
    )

    @Resource("categories")
    public data class Categories(val parent: SponsorblockRoute) {
        public constructor(guildId: ULong, sessionId: String) : this(
            SponsorblockRoute(
                guildId,
                sessionId
            )
        )
    }
}
