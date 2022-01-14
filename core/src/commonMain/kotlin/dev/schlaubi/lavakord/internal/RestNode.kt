package dev.schlaubi.lavakord.internal

import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.audio.RestNode
import io.ktor.http.*

internal class RestNodeImpl(
    override val host: Url,
    override val name: String,
    override val authenticationHeader: String,
    override val lavakord: LavaKord
) : RestNode
