package dev.schlaubi.lavakord.internal

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.*

/**
 * Js implementation of [HttpEngine].
 */
public actual object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig> by Js
