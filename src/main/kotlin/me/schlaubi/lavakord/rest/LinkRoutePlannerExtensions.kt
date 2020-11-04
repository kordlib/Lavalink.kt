package me.schlaubi.lavakord.rest

import kotlinx.serialization.SerializationException
import lavalink.client.io.Link
import me.schlaubi.lavakord.NoRoutePlannerException
import me.schlaubi.lavakord.asKordLink
import me.schlaubi.lavakord.audio.KordLink

/**
 * Retrieves the current address status of the route planner api. Can be null if no Route planner is set
 *
 * @see RoutePlannerStatus
 */
public suspend fun Link.addressStatusOrNull(): RoutePlannerStatus<out RoutePlannerStatus.Data>? =
    asKordLink().addressStatusOrNull()

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
public suspend fun Link.unmarkAllAddresses(): Unit = asKordLink().unmarkAllAddresses()

/**
 * Unmarks the route planner [address].
 */
public suspend fun Link.unmarkAddress(address: String): Unit = asKordLink().unmarkAddress(address)

private suspend fun KordLink.addressStatusOrNull(): RoutePlannerStatus<out RoutePlannerStatus.Data>? {
    val node = this.getNode(true) ?: error("No node available")
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

private suspend fun KordLink.unmarkAllAddresses() {
    val node = this.getNode(true) ?: error("No node available")
    val url = node.buildUrl {
        path("/routeplanner/free/all")
    }

    return node.get(url)
}

private suspend fun KordLink.unmarkAddress(address: String) {
    val node = this.getNode(true) ?: error("No node available")
    val url = node.buildUrl {
        path("/routeplanner/free/address")
    }

    return node.post(url) {
        body = mapOf("address" to address)
    }
}
