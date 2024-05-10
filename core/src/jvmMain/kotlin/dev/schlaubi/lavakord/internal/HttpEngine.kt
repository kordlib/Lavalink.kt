package dev.schlaubi.lavakord.internal

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*

public actual object HttpEngine : HttpClientEngineFactory<HttpClientEngineConfig> by OkHttp
