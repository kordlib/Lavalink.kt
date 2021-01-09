package me.schlaubi.lavakord

import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.extensions.lavalink.InsufficientPermissionException
import dev.kord.extensions.lavalink.LavaKord
import dev.kord.extensions.lavalink.audio.Link
import dev.kord.extensions.lavalink.kord.connectAudio
import dev.kord.extensions.lavalink.kord.getLink
import dev.kord.extensions.lavalink.kord.kordPermission

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
 * Connects this link to the [voiceChannel].
 */
@Deprecated("Moved to kord package", ReplaceWith("connectAudio", "dev.kord.extensions.lavalink.kord.connectAudio"))
public suspend fun Link.connect(voiceChannel: VoiceChannel): Unit = connectAudio(voiceChannel)

/**
 * Connects this link to the voice channel with the specified [snowflake].
 */
@Deprecated("Moved to kord package", ReplaceWith("connectAudio", "dev.kord.extensions.lavalink.kord.connectAudio"))
public suspend fun Link.connect(snowflake: Snowflake): Unit = connectAudio(snowflake)

/**
 * The [Permissions] that were missing.
 */
@Deprecated("moved to kord package", ReplaceWith("kordPermission", "dev.kord.extensions.lavalink.kord.kordPermission"))
public val InsufficientPermissionException.kordPermission: Permissions
    get() = this.kordPermission
