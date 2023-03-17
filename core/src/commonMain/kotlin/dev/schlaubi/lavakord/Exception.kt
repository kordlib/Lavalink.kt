package dev.schlaubi.lavakord

import dev.schlaubi.lavakord.Exception.Severity
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * An Exception reported from lavalink/lavaplayer.
 *
 * @property message the message of the error
 * @property severity the [Severity] of the error.
 * @property cause The cause of the exception
 */
@Serializable
public data class Exception(
    val message: String,
    val severity: Severity,
    val cause: String
) {
    /**
     * Severity levels for FriendlyException
     *
     * Credit: https://github.com/sedmelluq/lavaplayer/blob/master/main/src/main/java/com/sedmelluq/discord/lavaplayer/tools/FriendlyException.java
     */
    @Serializable
    @Suppress("unused")
    public enum class Severity {
        /**
         * The cause is known and expected, indicates that there is nothing wrong with the library itself.
         */
        COMMON,

        /**
         * The cause might not be exactly known, but is possibly caused by outside factors. For example when an outside
         * service responds in a format that we do not expect.
         */
        SUSPICIOUS,

        /**
         * If the probable cause is an issue with the library or when there is no way to tell what the cause might be.
         * This is the default level and other levels are used in cases where the thrower has more in-depth knowledge
         * about the error.
         */
        FAULT
    }
}

/**
 * Representation of a REST error.
 *
 * @property timestamp the [Instant] in which the error happened
 * @property status The HTTP status code
 * @property error The HTTP status code message
 * @property trace The stack trace of the error when trace=true as query param
 *                  has been sent (See [LavaKordOptions.LinkConfig.showTrace])
 * @property message The error message
 * @property path The request path
 */
@Serializable
public data class RestError(
    val timestamp: Instant,
    val status: Int,
    val error: String,
    val trace: String?,
    val message: String,
    val path: String
)
