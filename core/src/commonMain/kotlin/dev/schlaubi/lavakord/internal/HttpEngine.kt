package dev.schlaubi.lavakord.internal

import io.ktor.client.engine.*

/**
 * This is the internally used Http Engine
 */
public expect object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig>
