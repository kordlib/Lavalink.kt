package dev.kord.x.lavalink.rest

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import dev.kord.x.lavalink.audio.Node
import dev.kord.x.lavalink.internal.HttpEngine
import mu.KotlinLogging

private val LOG = KotlinLogging.logger {}

internal val client = HttpClient(HttpEngine) {
    Json {
        val json = kotlinx.serialization.json.Json {
            serializersModule = RoutePlannerModule
        }

        serializer = KotlinxSerializer(json)
    }

    install(Logging) {
        level = LogLevel.ALL
        logger = object : Logger {
            override fun log(message: String) = LOG.trace { message }
        }
    }

    install(Logging) {
        level = LogLevel.INFO
        logger = object : Logger {
            override fun log(message: String) = LOG.debug { message }
        }
    }
}

internal suspend inline fun <reified T> Node.get(noinline urlBuilder: URLBuilder.() -> Unit) =
    client.get<T>(buildUrl(urlBuilder).build()) { addHeader(this@get) }

internal suspend inline fun <reified T> Node.post(
    urlBuilder: URLBuilder,
    block: HttpRequestBuilder.() -> Unit
) =
    client.get<T>(urlBuilder.build()) { addHeader(this@post); block(this) }

private fun HttpRequestBuilder.addHeader(socket: Node) {
    headers["Authorization"] = socket.authenticationHeader
}

internal fun Node.buildUrl(block: URLBuilder.() -> Unit) = URLBuilder(host)
    .apply {
        protocol = if (protocol.isSecure()) protocol.copy(name = "https") else protocol.copy(name = "http")
    }
    .apply(block)
