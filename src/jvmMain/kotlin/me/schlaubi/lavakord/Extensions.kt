package me.schlaubi.lavakord

import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.extensions.lavalink.InsufficientPermissionException
import dev.kord.extensions.lavalink.LavaKord
import dev.kord.extensions.lavalink.audio.Link

/**
 * Creates or returns an existing [Link] for the guild with the specified [guildId].
 *
 * @see LavaKord.getLink
 */
@Deprecated("Migrated to kord package", ReplaceWith("getLink", "dev.kord.extensions.lavalink.kord.getLink"))
public fun LavaKord.getLink(guildId: Snowflake): Link = getLink(guildId)

/**
 * Creates or returns an existing [Link] for this [Guild] using the [lavalink] instance.
 */
@Deprecated("Migrated to kord package", ReplaceWith("getLink", "dev.kord.extensions.lavalink.kord.getLink"))
public fun GuildBehavior.getLink(lavalink: LavaKord): Link = getLink(lavalink)

/**
 * The [Permissions] that were missing.
 */
@Deprecated("moved to kord package", ReplaceWith("kordPermission", "dev.kord.extensions.lavalink.kord.kordPermission"))
public val InsufficientPermissionException.kordPermission: Permissions
    get() = this.kordPermission
