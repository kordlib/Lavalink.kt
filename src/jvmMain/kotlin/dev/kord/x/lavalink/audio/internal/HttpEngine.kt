package dev.kord.x.lavalink.audio.internal

import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.util.*

@OptIn(KtorExperimentalAPI::class)
public actual object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig> by CIO
