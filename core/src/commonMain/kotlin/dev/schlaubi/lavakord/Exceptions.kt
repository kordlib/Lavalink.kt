package dev.schlaubi.lavakord

import dev.arbjerg.lavalink.protocol.v4.Error
import dev.schlaubi.lavakord.audio.Link
import io.ktor.client.request.*

/**
 * Exception thrown when there is no permission to perform a certain action.
 *
 * @property permission the required but missing permission
 */
public class InsufficientPermissionException(@Suppress("MemberVisibilityCanBePrivate") public val permission: String) :
    IllegalStateException("Action cannot be performed due to lack of $permission")

/**
 * Exception thrown on [Link.addressStatus] when there is no route planner set in Lavalink configuration.
 */
public class NoRoutePlannerException : IllegalStateException("No route planner was set")

/**
 * Exception indicating a REST request failed.
 *
 * @property error the [RestError] response
 * @property request the [HttpRequest] which failed
 */
public data class RestException(val error: Error, val request: HttpRequest) : IllegalStateException(error.message)
