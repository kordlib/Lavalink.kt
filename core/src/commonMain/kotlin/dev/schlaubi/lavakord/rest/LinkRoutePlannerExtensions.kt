package dev.schlaubi.lavakord.rest

import dev.arbjerg.lavalink.protocol.v4.RoutePlannerFreeAddress
import dev.arbjerg.lavalink.protocol.v4.RoutePlannerStatus
import dev.schlaubi.lavakord.NoRoutePlannerException
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.RestNode
import dev.schlaubi.lavakord.rest.routes.V4Api
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Retrieves the current address status of the route planner api. Can be null if no Route planner is set
 *
 * @see RoutePlannerStatus
 * @see Node.addressStatusOrNull
 */
public suspend fun Link.addressStatusOrNull(): RoutePlannerStatus? =
    node.addressStatusOrNull()

/**
}
 * Retrieves the current address status of the route planner api. Can be null if no Route planner is set
 *
 * @see RoutePlannerStatus
 */
public suspend fun RestNode.addressStatusOrNull(): RoutePlannerStatus? =
    get(V4Api.RoutePlanner.Status())

/**
 * Retrieves the current address status of the route planner api.
 *
 * @throws NoRoutePlannerException when there is no Route planner specified in Lavalink configuration
 *
 * @see Node.addressStatus
 * @see NoRoutePlannerException
 * @see RoutePlannerStatus
 */
public suspend fun Link.addressStatus(): RoutePlannerStatus = node.addressStatus()

/**
 * Retrieves the current address status of the route planner api.
 *
 * @throws NoRoutePlannerException when there is no Route planner specified in Lavalink configuration
 *
 * @see Link.addressStatusOrNull
 * @see NoRoutePlannerException
 * @see RoutePlannerStatus
 */
public suspend fun RestNode.addressStatus(): RoutePlannerStatus =
    addressStatusOrNull() ?: throw NoRoutePlannerException()

/**
 * Unmarks all failed route planner addresses.
 *
 * @see Node.unmarkAllAddresses
 */
public suspend fun Link.unmarkAllAddresses(): Unit = node.unmarkAllAddresses()

/**
 * Unmarks all failed route planner addresses.
 */
public suspend fun RestNode.unmarkAllAddresses(): Unit = post(V4Api.RoutePlanner.Free.All())

/**
 * Unmarks the route planner [address].
 *
 * @see Node.unmarkAddress
 */
public suspend fun Link.unmarkAddress(address: String): Unit = node.unmarkAddress(address)

/**
 * Unmarks the route planner [address].
 */
public suspend fun RestNode.unmarkAddress(address: String) {
    return post(V4Api.RoutePlanner.Free.Address()) {
        contentType(ContentType.Application.Json)
        setBody(RoutePlannerFreeAddress(address))
    }
}
