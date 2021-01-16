package dev.kord.x.lavalink.internal

import io.ktor.client.engine.*

/**
 * This is the internally used Http Engine
 */
public expect object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig>
