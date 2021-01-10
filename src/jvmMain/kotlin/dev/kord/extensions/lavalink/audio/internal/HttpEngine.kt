package dev.kord.extensions.lavalink.audio.internal

import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*

public actual object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig> by CIO
