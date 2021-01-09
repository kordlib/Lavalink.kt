package me.schlaubi.lavakord

import dev.kord.extensions.lavalink.audio.internal.WebsocketPlayer
import dev.kord.extensions.lavalink.audio.player.Player
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
internal fun Player.checkImplementation() {
    contract {
        returns() implies (this@checkImplementation is WebsocketPlayer)
    }
    require(this is WebsocketPlayer) { "This has to be a internal implementation instance" }
}
