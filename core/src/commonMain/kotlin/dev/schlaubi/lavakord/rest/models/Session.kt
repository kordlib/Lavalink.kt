package dev.schlaubi.lavakord.rest.models

import kotlinx.serialization.Serializable

/**
 * Representation of a session.
 *
 * @property resumingKey the currently configured resuming key
 * @property timeout the resume timeout
 */
@Serializable
public data class Session(
    val resumingKey: String? = null,
    val timeout: Int
)
