package me.schlaubi.lavakord

import me.schlaubi.lavakord.audio.impl.WebsocketPlayer
import me.schlaubi.lavakord.audio.player.Player
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
internal fun Player.checkImplementation() {
    contract {
        returns() implies (this@checkImplementation is WebsocketPlayer)
    }
    require(this is WebsocketPlayer) { "This has to be a internal implementation instance" }
}
