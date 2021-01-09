package dev.kord.extensions.lavalink.rest

import dev.kord.extensions.lavalink.NoRoutePlannerException
import dev.kord.extensions.lavalink.audio.Link
import kotlinx.serialization.SerializationException

/**
 * Retrieves the current address status of the route planner api. Can be null if no Route planner is set
 *
 * @see RoutePlannerStatus
 */
public suspend fun Link.addressStatusOrNull(): RoutePlannerStatus<out RoutePlannerStatus.Data>? {
    val url = node.buildUrl {
        path("/routeplanner/status")
    }

    return try {
        node.get(url)
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
 * @see Link.addressStatusOrNull
 * @see NoRoutePlannerException
 * @see RoutePlannerStatus
 */
public suspend fun Link.addressStatus(): RoutePlannerStatus<out RoutePlannerStatus.Data> =
    addressStatusOrNull() ?: throw NoRoutePlannerException()

/**
 * Unmarks all route planner addresses.
 */
public suspend fun Link.unmarkAllAddresses() {
    val url = node.buildUrl {
        path("/routeplanner/free/all")
    }

    return node.get(url)
}

/**
 * Unmarks the route planner [address].
 */
private suspend fun Link.unmarkAddress(address: String) {
    val url = node.buildUrl {
        path("/routeplanner/free/address")
    }

    return node.post(url) {
        body = mapOf("address" to address)
    }
}
