package dev.schlaubi.lavakord

/**
 * Marks a declaration as part of the low-level REST api mapping.
 */
@RequiresOptIn("This API is an unsafe low-level mapping of the Lavalink REST API! Use with care.")
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
public annotation class UnsafeRestApi

/**
 * Marks a declaration as part of the plugin api.
 */
@RequiresOptIn("This API is only intended to be used by plugins")
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
public annotation class PluginApi
