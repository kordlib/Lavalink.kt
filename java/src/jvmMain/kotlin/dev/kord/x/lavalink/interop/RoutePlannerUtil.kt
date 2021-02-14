@file:JvmName("RoutePlannerUtil")

package dev.kord.x.lavalink.interop

import dev.kord.x.lavalink.NoRoutePlannerException
import dev.kord.x.lavalink.audio.Node
import dev.kord.x.lavalink.rest.RoutePlannerStatus
import dev.kord.x.lavalink.rest.addressStatusOrNull
import dev.kord.x.lavalink.rest.unmarkAddress
import dev.kord.x.lavalink.rest.unmarkAllAddresses
import java.util.concurrent.CompletableFuture

private fun Node.javaAddressStatusOrNull() =
    lavakord.supply { addressStatusOrNull() }

/**
 * Retrieves the current address status of the route planner api. Can be null if no Route planner is set.
 *
 * @param node the [Node] to retrieve the [RoutePlannerStatus] from
 *
 * @see RoutePlannerStatus
 * @see Node.addressStatusOrNull
 */
public fun addressStatusOrNull(node: Node): CompletableFuture<RoutePlannerStatus<out RoutePlannerStatus.Data>?> =
    node.javaAddressStatusOrNull()

/**
 * Retrieves the current address status of the route planner api. Can be null if no Route planner is set.
 *
 * @param node the [Node] to retrieve the [RoutePlannerStatus] from
 *
 * @throws NoRoutePlannerException when no route planner is set
 * @see RoutePlannerStatus
 * @see Node.addressStatusOrNull
 */
public fun addressStatus(node: Node): CompletableFuture<RoutePlannerStatus<out RoutePlannerStatus.Data>> =
    node.javaAddressStatusOrNull().thenApply { it ?: throw NoRoutePlannerException() }

private fun Node.javaUnmarkAllAddresses() = lavakord.run { unmarkAllAddresses() }

/**
 * Unmarks all failed route planner addresses.
 *
 * @param node the [Node] to unmark all addresses on
 */
public fun unmarkAllAddresses(node: Node): CompletableFuture<Void> = node.javaUnmarkAllAddresses()

/**
 * Unmarks all failed route planner addresses.
 *
 * @param link the [JavaLink] to unmark all addresses on
 */
public fun unmarkAllAddresses(link: JavaLink): CompletableFuture<Void> =
    link.node.javaUnmarkAllAddresses()

private fun Node.javaUnmarkAddress(address: String) = lavakord.run {
    unmarkAddress(address)
}

/**
 * Unmarks the route planner [address].
 *
 * @param node the [Node] to unmark [address] on
 */
public fun unmarkAddress(node: Node, address: String): CompletableFuture<Void> = node.javaUnmarkAddress(address)

/**
 * Unmarks the route planner [address].
 *
 * @param link the [JavaLink] to unmark [address] on
 */
public fun unmarkAddress(link: JavaLink, address: String): CompletableFuture<Void> =
    link.node.javaUnmarkAddress(address)
