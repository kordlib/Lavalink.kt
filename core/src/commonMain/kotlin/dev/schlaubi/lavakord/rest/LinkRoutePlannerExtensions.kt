package dev.schlaubi.lavakord.rest

import dev.schlaubi.lavakord.NoRoutePlannerException
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.RestNode
import dev.schlaubi.lavakord.rest.routes.V3Api
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Retrieves the current address status of the route planner api. Can be null if no Route planner is set
 *
 * @see RoutePlannerStatus
 * @see Node.addressStatusOrNull
 */
public suspend fun Link.addressStatusOrNull(): RoutePlannerStatus<out RoutePlannerStatus.Data>? =
    node.addressStatusOrNull()

/**
}
 * Retrieves the current address status of the route planner api. Can be null if no Route planner is set
 *
 * @see RoutePlannerStatus
 */
public suspend fun RestNode.addressStatusOrNull(): RoutePlannerStatus<out RoutePlannerStatus.Data>? {
    return try {
        val response = get<JsonElement, _>(V3Api.RoutePlanner.Status())
        // Due to a bug in ktor kx.ser doesn't get the correct info on K/JS and fails
        json.decodeFromJsonElement<RoutePlannerStatus<out RoutePlannerStatus.Data>>(response)
    } catch (e: SerializationException) {
        if (e.message?.endsWith("{}") == true) { // {} means no route planer is not set
            return null
        } else throw e
    }
}

/**
 * Retrieves the current address status of the route planner api.
 *
 * @throws NoRoutePlannerException when there is no Route planner specified in Lavalink configuration
 *
 * @see Node.addressStatus
 * @see NoRoutePlannerException
 * @see RoutePlannerStatus
 */
public suspend fun Link.addressStatus(): RoutePlannerStatus<out RoutePlannerStatus.Data> = node.addressStatus()

/**
 * Retrieves the current address status of the route planner api.
 *
 * @throws NoRoutePlannerException when there is no Route planner specified in Lavalink configuration
 *
 * @see Link.addressStatusOrNull
 * @see NoRoutePlannerException
 * @see RoutePlannerStatus
 */
public suspend fun RestNode.addressStatus(): RoutePlannerStatus<out RoutePlannerStatus.Data> =
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
public suspend fun RestNode.unmarkAllAddresses(): Unit = post(V3Api.RoutePlanner.Free.All())

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
    return post(V3Api.RoutePlanner.Free.Address()) {
        contentType(ContentType.Application.Json)
        setBody(UnmarkAddressBody(address))
    }
}

@Serializable
private class UnmarkAddressBody(@Suppress("unused") val address: String)
