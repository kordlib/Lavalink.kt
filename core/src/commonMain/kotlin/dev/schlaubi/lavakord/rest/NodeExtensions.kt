package dev.schlaubi.lavakord.rest

import dev.arbjerg.lavalink.protocol.v4.Info
import dev.arbjerg.lavalink.protocol.v4.Stats
import dev.arbjerg.lavalink.protocol.v4.StatsData
import dev.schlaubi.lavakord.audio.RestNode
import dev.schlaubi.lavakord.audio.StatsEvent
import dev.schlaubi.lavakord.rest.routes.V4Api
import dev.schlaubi.lavakord.rest.routes.Version

/**
 * Request [Lavalink information][Info].
 */
public suspend fun RestNode.getInfo(): Info = get(V4Api.Info())

/**
 * Request [Lavalink statistics][StatsEvent].
 */
public suspend fun RestNode.getStats(): Stats = get<StatsData, _>(V4Api.Stats())

/**
 * Returns the plain version string of the Lavalink Server.
 *
 * @see Info.version
 * @see getInfo
 */
public suspend fun RestNode.getVersion(): String = get(Version())
