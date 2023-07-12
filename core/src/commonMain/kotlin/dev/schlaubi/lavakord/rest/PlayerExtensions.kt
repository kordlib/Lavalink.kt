package dev.schlaubi.lavakord.rest

import dev.arbjerg.lavalink.protocol.v4.Player
import dev.arbjerg.lavalink.protocol.v4.PlayerUpdate
import dev.schlaubi.lavakord.UnsafeRestApi
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.RestNode
import dev.schlaubi.lavakord.rest.routes.V4Api
import io.ktor.client.request.*

/**
 * Returns the [Player] for this guild in this session.
 */
public suspend fun RestNode.getPlayer(guildId: ULong, sessionId: String): Player =
    get(V4Api.Sessions.Specific.Players.Specific(guildId, sessionId))

/**
 * Returns the [Player] for this guild in this session.
 */
public suspend fun Node.getPlayer(guildId: ULong): Player = getPlayer(guildId, sessionId)

/**
 * Updates or creates the player for this guild if it doesn't already exist.
 */
@UnsafeRestApi
public suspend fun RestNode.updatePlayer(
    guildId: ULong,
    sessionId: String,
    noReplace: Boolean? = null,
    request: PlayerUpdate
): Player =
    patch(V4Api.Sessions.Specific.Players.Specific(guildId, sessionId, noReplace)) {
        setBody(request)
    }

/**
 * Updates or creates the player for this guild if it doesn't already exist.
 */
@UnsafeRestApi
public suspend fun Node.updatePlayer(
    guildId: ULong,
    noReplace: Boolean? = null,
    request: PlayerUpdate
): Player = updatePlayer(guildId, sessionId, noReplace, request)

/**
 * Destroys the player for this guild in this session.
 */
@UnsafeRestApi
public suspend fun RestNode.destroyPlayer(guildId: ULong, sessionId: String): Unit =
    delete(V4Api.Sessions.Specific.Players.Specific(guildId, sessionId))

/**
 * Destroys the player for this guild in this session.
 */
@UnsafeRestApi
public suspend fun Node.destroyPlayer(guildId: ULong): Unit = destroyPlayer(guildId, sessionId)
