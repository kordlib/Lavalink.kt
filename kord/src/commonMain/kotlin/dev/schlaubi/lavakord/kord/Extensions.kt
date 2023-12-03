package dev.schlaubi.lavakord.kord

import dev.arbjerg.lavalink.protocol.v4.Player
import dev.arbjerg.lavalink.protocol.v4.PlayerUpdate
import dev.kord.common.DiscordBitSet
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.VoiceChannel
import dev.schlaubi.lavakord.InsufficientPermissionException
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.UnsafeRestApi
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.RestNode
import dev.schlaubi.lavakord.rest.destroyPlayer
import dev.schlaubi.lavakord.rest.getPlayer
import dev.schlaubi.lavakord.rest.updatePlayer

/**
 * Creates or returns an existing [Link] for the guild with the specified [guildId].
 *
 * @see LavaKord.getLink
 */
public fun LavaKord.getLink(guildId: Snowflake): Link = getLink(guildId.value)

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
    get() = Permissions.Builder(DiscordBitSet(permission)).build()


/**
 * Returns the [Player] for this guild in this session.
 */
public suspend fun RestNode.getPlayer(guildId: Snowflake, sessionId: String): Player =
    getPlayer(guildId.value, sessionId)

/**
 * Returns the [Player] for this guild in this session.
 */
public suspend fun Node.getPlayer(guildId: Snowflake): Player = getPlayer(guildId.value)

/**
 * Updates or creates the player for this guild if it doesn't already exist.
 */
@UnsafeRestApi
public suspend fun RestNode.updatePlayer(
    guildId: Snowflake,
    sessionId: String,
    noReplace: Boolean? = null,
    request: PlayerUpdate
): Player = updatePlayer(guildId.value, sessionId, noReplace, request)

/**
 * Updates or creates the player for this guild if it doesn't already exist.
 */
@UnsafeRestApi
public suspend fun Node.updatePlayer(
    guildId: Snowflake,
    noReplace: Boolean? = null,
    request: PlayerUpdate
): Player = updatePlayer(guildId.value, noReplace, request)

/**
 * Destroys the player for this guild in this session.
 */
@UnsafeRestApi
public suspend fun RestNode.destroyPlayer(guildId: Snowflake, sessionId: String): Unit =
    destroyPlayer(guildId.value, sessionId)

/**
 * Destroys the player for this guild in this session.
 */
@UnsafeRestApi
public suspend fun Node.destroyPlayer(guildId: Snowflake): Unit = destroyPlayer(guildId.value)
