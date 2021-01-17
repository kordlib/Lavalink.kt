package me.schlaubi.lavakord

import dev.kord.x.lavalink.InsufficientPermissionException
import dev.kord.x.lavalink.NoRoutePlannerException
import dev.kord.x.lavalink.RemoteTrackException
import dev.kord.x.lavalink.rest.addressStatus
import dev.kord.x.lavalink.audio.Link

/**
 * Exception thrown when there is no permission to perform a certain action.
 *
 * @see InsufficientPermissionException
 */
@Deprecated(
    "Moved to new package",
    ReplaceWith("InsufficientPermissionException", "dev.kord.extensions.lavalink.InsufficientPermissionException")
)
public typealias InsufficientPermissionException = InsufficientPermissionException

/**
 * Exception thrown on [Link.addressStatus] when there is no route planner set in Lavalink configuration.
 *
 * @see InsufficientPermissionException
 */
@Deprecated(
    "Moved to new package",
    ReplaceWith("NoRoutePlannerException", "dev.kord.extensions.lavalink.NoRoutePlannerException")
)
public typealias NoRoutePlannerException = NoRoutePlannerException

/**
 * Forwarded Exception from Lavalink player thrown if an error occurs whilst playing a track.
 *
 * @see InsufficientPermissionException
 */
@Deprecated(
    "Moved to new package",
    ReplaceWith("RemoteTrackException", "dev.kord.extensions.lavalink.RemoteTrackException")
)
public typealias RemoteTrackException = RemoteTrackException
