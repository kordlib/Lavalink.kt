package dev.schlaubi.lavakord.rest

import dev.arbjerg.lavalink.protocol.v4.Session
import dev.arbjerg.lavalink.protocol.v4.SessionUpdate
import dev.schlaubi.lavakord.UnsafeRestApi
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.RestNode
import dev.schlaubi.lavakord.rest.routes.V4Api
import io.ktor.client.request.*

/**
 * Updates the current session.
 */
@UnsafeRestApi
public suspend fun RestNode.updateSession(sessionId: String, request: SessionUpdate): Session = patch(
    V4Api.Sessions.Specific(sessionId)
) {
    setBody(request)
}

/**
 * Updates the current session.
 */
@UnsafeRestApi
public suspend fun Node.updateSession(request: SessionUpdate): Session = updateSession(sessionId, request)
