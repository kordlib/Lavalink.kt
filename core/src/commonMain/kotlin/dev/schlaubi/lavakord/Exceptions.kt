package dev.schlaubi.lavakord

import dev.schlaubi.lavakord.audio.Link


/**
 * Exception thrown when there is no permission to perform a certain action.
 *
 * @property permission the required but missing permission
 */
public class InsufficientPermissionException(@Suppress("MemberVisibilityCanBePrivate") public val permission: String) :
    RuntimeException("Action cannot be performed due to lack of $permission")

/**
 * Exception thrown on [Link.addressStatus] when there is no route planner set in Lavalink configuration.
 */
public class NoRoutePlannerException : RuntimeException("No route planner was set")

/**
 * Forwarded Exception from Lavalink player thrown if an error occurs whilst playing a track.
 */
public class RemoteTrackException(message: String? = null) : RuntimeException(message)