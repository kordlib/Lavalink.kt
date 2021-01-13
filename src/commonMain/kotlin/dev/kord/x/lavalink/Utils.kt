package dev.kord.x.lavalink

import dev.kord.x.lavalink.audio.internal.WebsocketPlayer
import dev.kord.x.lavalink.audio.player.Player
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
    var v: V?
    if (get(key).also { v = it } == null) {
        var newValue: V?
        if (mappingFunction(key).also { newValue = it } != null) {
            @Suppress("ReplaceNotNullAssertionWithElvisReturn") // see also call above
            put(key, newValue!!)
            return newValue!!
        }
    }
    return v!!
}

