package me.schlaubi.lavakord

import com.gitlab.kordlib.common.entity.Permission

/**
 * Exception thrown when there is no permission to perform a certain action.
 *
 * @property permission the required but missing permission
 */
class InsufficientPermissionException(@Suppress("MemberVisibilityCanBePrivate") val permission: Permission) :
    RuntimeException("Action cannot be performed due to lack of $permission")
