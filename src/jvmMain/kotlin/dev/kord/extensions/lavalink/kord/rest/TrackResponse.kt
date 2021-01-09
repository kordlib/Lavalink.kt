package dev.kord.extensions.lavalink.kord.rest

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import dev.kord.extensions.lavalink.rest.TrackResponse
import lavalink.client.LavalinkUtil

/**
 * Converts this track to an [AudioTrack].
 */
@Deprecated("Replaced with toTrack() as LavaPlayer is being removed", ReplaceWith("toTrack"))
public fun TrackResponse.PartialTrack.toAudioTrack(): AudioTrack = LavalinkUtil.toAudioTrack(track)

/**
 * @see FriendlyException
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("FriendlyExceptionSupport is being discontinued as of removal of all JVM dependencies")
public fun TrackResponse.Error.toFriendlyException(): FriendlyException = FriendlyException(
    message, FriendlyException.Severity.valueOf(severity.toString()), null
)
