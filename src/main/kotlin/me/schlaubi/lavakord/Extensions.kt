package me.schlaubi.lavakord

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.VoiceChannel
import me.schlaubi.lavakord.audio.Link
import me.schlaubi.lavakord.kord.connectAudio

/**
 * Connects this link to the [voiceChannel].
 */
@Deprecated("Moved to kord package", ReplaceWith("connectAudio", "me.schlaubi.lavakord.kord.connectAudio"))
public suspend fun Link.connect(voiceChannel: VoiceChannel): Unit = connectAudio(voiceChannel)

/**
 * Connects this link to the voice channel with the specified [snowflake].
 */
@Deprecated("Moved to kord package", ReplaceWith("connectAudio", "me.schlaubi.lavakord.kord.connectAudio"))
public suspend fun Link.connect(snowflake: Snowflake): Unit = connectAudio(snowflake)
