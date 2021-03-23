package dev.schlaubi.lavakord

import dev.schlaubi.lavakord.audio.internal.WebsocketPlayer
import dev.schlaubi.lavakord.audio.player.Player
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
internal fun Player.checkImplementation() {
    contract {
        returns() implies (this@checkImplementation is WebsocketPlayer)
    }
    require(this is WebsocketPlayer) { "This has to be a internal implementation instance" }
}

/**
 * Port of Java [Map.computeIfAbsent](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html#computeIfAbsent-K-java.util.function.Function-) to Kotlin MPP.
 */
public fun <K, V> MutableMap<K, V>.computeIfAbsent(
    key: K,
    mappingFunction: (K) -> V
): V {
    val v = get(key)
    if (v == null) {
        val newValue = mappingFunction(key)
        if (newValue != null) {
            put(key, newValue)
            return newValue
        }
    }

    return v!!
}

