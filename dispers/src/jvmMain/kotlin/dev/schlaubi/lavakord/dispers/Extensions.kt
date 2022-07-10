package dev.schlaubi.lavakord.dispers

import dev.bitflow.dispers.client.amqp.SnowflakedDiscordGuild
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Guild
import dev.schlaubi.lavakord.InsufficientPermissionException
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.audio.Link

/**
 * Creates or returns an existing [Link] for the guild with the specified [guildId].
 *
 * @see LavaKord.getLink
 */
public fun LavaKord.getLink(guildId: Snowflake): Link = getLink(guildId.toString())

/**
 * Creates or returns an existing [Link] for this [Guild] using the [lavalink] instance.
 */
public fun SnowflakedDiscordGuild.getLink(lavalink: LavaKord): Link = lavalink.getLink(id)

/**
 * Connects this link to the voice channel with the specified [snowflake].
 */
public suspend fun Link.connectAudio(snowflake: Snowflake): Unit = connectAudio(snowflake.value)

/**
 * The [Permissions] that were missing.
 */
public val InsufficientPermissionException.kordPermission: Permissions
    get() = Permissions(permission)
