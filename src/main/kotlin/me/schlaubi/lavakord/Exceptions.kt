package me.schlaubi.lavakord

import com.gitlab.kordlib.common.entity.Permission
import lavalink.client.io.Link
import me.schlaubi.lavakord.rest.addressStatus

/**
 * Exception thrown when there is no permission to perform a certain action.
 *
 * @property permission the required but missing permission
 */
public class InsufficientPermissionException(@Suppress("MemberVisibilityCanBePrivate") public val permission: Permission) :
    RuntimeException("Action cannot be performed due to lack of $permission")

/**
 * Exception thrown on [Link.addressStatus] when there is no route planner set in Lavalink configuration.
 */
public class NoRoutePlannerException : RuntimeException("No route planner was set")
