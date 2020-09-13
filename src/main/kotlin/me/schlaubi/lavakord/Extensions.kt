package me.schlaubi.lavakord

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.channel.VoiceChannel
import lavalink.client.io.Lavalink
import lavalink.client.io.Link
import me.schlaubi.lavakord.audio.KordLink

/**
 * Creates or returns an existing [Link] for the guild with the specified [guildId].
 *
 * @see Lavalink.getLink
 */
fun <T : Link> Lavalink<T>.getLink(guildId: Snowflake): T = getLink(guildId.value)

/**
 * Creates or returns an existing [Link] for this [Guild] using the [lavalink] instance.
 */
fun GuildBehavior.getLink(lavalink: Lavalink<out Link>): Link = lavalink.getLink(id)

/**
 * Connects this link to the [voiceChannel].
 */
suspend fun Link.connect(voiceChannel: VoiceChannel): Unit = connect(voiceChannel.id)

/**
 * Connects this link to the voice channel with the specified [snowflake].
 */
suspend fun Link.connect(snowflake: Snowflake): Unit = connect(snowflake.value)

/**
 * Connects this link to the voice channel with the specified [voiceChannelId].
 */
suspend fun Link.connect(voiceChannelId: String): Unit = connect(voiceChannelId.toLong())

/**
 * Connects this link to the voice channel with the specified [voiceChannelId]
 *
 * @throws me.schlaubi.lavakord.InsufficientPermissionException if the bot does not have the permission to join the voice channel or override user limit if needed
 * @throws NullPointerException If the [voiceChannelId] does not resolve to a valid voice channel
 */
suspend fun Link.connect(voiceChannelId: Long): Unit =
    asKordLink().connect(voiceChannelId, true)

internal fun Link.asKordLink(): KordLink = (this as? KordLink) ?: error("This cannot be used on non kord links")
