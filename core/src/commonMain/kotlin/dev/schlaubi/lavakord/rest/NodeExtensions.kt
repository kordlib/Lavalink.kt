package dev.schlaubi.lavakord.rest

import dev.schlaubi.lavakord.audio.RestNode
import dev.schlaubi.lavakord.audio.StatsEvent
import dev.schlaubi.lavakord.audio.internal.GatewayPayload
import dev.schlaubi.lavakord.rest.models.Info
import dev.schlaubi.lavakord.rest.routes.V3Api
import dev.schlaubi.lavakord.rest.routes.Version

/**
 * Request [Lavalink information][Info].
 */
public suspend fun RestNode.getInfo(): Info = get(V3Api.Info())

/**
 * Request [Lavalink statistics][StatsEvent].
 */
public suspend fun RestNode.getStats(): StatsEvent = get<GatewayPayload.StatsEvent, _>(V3Api.Stats())

/**
 * Returns the plain version string of the Lavalink Server.
 *
 * @see Info.version
 * @see getInfo
 */
public suspend fun RestNode.getVersion(): String = get(Version())
