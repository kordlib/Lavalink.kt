package dev.kord.x.lavalink.internal

import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.util.*

@OptIn(KtorExperimentalAPI::class)
internal actual object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig> by CIO
