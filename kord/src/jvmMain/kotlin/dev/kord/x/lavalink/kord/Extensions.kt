package dev.kord.x.lavalink.kord

import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.x.lavalink.InsufficientPermissionException
import dev.kord.x.lavalink.LavaKord
import dev.kord.x.lavalink.audio.Link

/**
 * Creates or returns an existing [Link] for the guild with the specified [guildId].
 *
 * @see LavaKord.getLink
 */
public fun LavaKord.getLink(guildId: Snowflake): Link = getLink(guildId.asString)

/**
 * Creates or returns an existing [Link] for this [Guild] using the [lavalink] instance.
 */
public fun GuildBehavior.getLink(lavalink: LavaKord): Link = lavalink.getLink(id)

/**
 * Connects this link to the [voiceChannel].
 */
public suspend fun Link.connectAudio(voiceChannel: VoiceChannel): Unit = connectAudio(voiceChannel.id)

/**
 * Connects this link to the voice channel with the specified [snowflake].
 */
public suspend fun Link.connectAudio(snowflake: Snowflake): Unit = connectAudio(snowflake.value)

/**
 * The [Permissions] that were missing.
 */
public val InsufficientPermissionException.kordPermission: Permissions
    get() = Permissions(permission)
