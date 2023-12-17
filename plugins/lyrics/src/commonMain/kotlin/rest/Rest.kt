package dev.schlaubi.lavakord.plugins.lyrics.rest

import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.player.Player
import dev.schlaubi.lavakord.audio.player.guildId
import dev.schlaubi.lavakord.audio.player.node
import dev.schlaubi.lavakord.rest.get
import dev.schlaubi.lyrics.protocol.Lyrics
import dev.schlaubi.lyrics.protocol.SearchTrack

/**
 * Requests the lyrics by [videoId].
 */
public suspend fun Node.requestLyrics(videoId: String): Lyrics = get(LyricsRoute.Video(videoId))

/**
 * Searches for [query].
 */
public suspend fun Node.searchLyrics(query: String): List<SearchTrack> = get(LyricsRoute.Search(query))

/**
 * Requests the lyrics of the currently playing track.
 */
public suspend fun Player.requestLyrics(): Lyrics = node.get(LyricsRoute.CurrentLyrics(node.sessionId, guildId))
