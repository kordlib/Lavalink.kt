package dev.schlaubi.lavakord

/**
 * Marks a declaration as part of the low-level REST api mapping.
 */
@RequiresOptIn("This API is an unsafe low-level mapping of the Lavalink REST API! Use with care.")
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
public annotation class UnsafeRestApi
