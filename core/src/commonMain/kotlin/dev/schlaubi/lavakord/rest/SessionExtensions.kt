package dev.schlaubi.lavakord.rest

import dev.schlaubi.lavakord.UnsafeRestApi
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.RestNode
import dev.schlaubi.lavakord.rest.models.Session
import dev.schlaubi.lavakord.rest.models.UpdateSessionRequest
import dev.schlaubi.lavakord.rest.routes.V3Api
import io.ktor.client.request.*

/**
 * Updates the current session.
 */
@UnsafeRestApi
public suspend fun RestNode.updateSession(sessionId: String, request: UpdateSessionRequest): Session = patch(
    V3Api.Sessions.Specific(sessionId)
) {
    setBody(request)
}

/**
 * Updates the current session.
 */
@UnsafeRestApi
public suspend fun Node.updateSession(request: UpdateSessionRequest): Session = updateSession(sessionId, request)
