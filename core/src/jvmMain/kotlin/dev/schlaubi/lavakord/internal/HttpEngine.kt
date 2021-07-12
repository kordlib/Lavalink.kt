package dev.schlaubi.lavakord.internal

import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.util.*

public actual object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig> by CIO
