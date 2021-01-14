package dev.kord.x.lavalink.internal

import io.ktor.client.engine.*

internal expect object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig>
