package dev.schlaubi.lavakord.rest

import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.internal.AbstractLavakord
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging

internal suspend inline fun <reified T> Node.get(noinline urlBuilder: URLBuilder.() -> Unit): T =
    restClient.get(buildUrl(urlBuilder).build()) { addHeader(this@get); accept(ContentType.Application.JavaScript) }

internal suspend inline fun <reified T> Node.post(
    urlBuilder: URLBuilder,
    block: HttpRequestBuilder.() -> Unit
) =
    restClient.get<T>(urlBuilder.build()) { addHeader(this@post); accept(ContentType.Application.JavaScript); block(this) }

private fun HttpRequestBuilder.addHeader(socket: Node) {
    header(HttpHeaders.Authorization, socket.authenticationHeader)
}

internal fun Node.buildUrl(block: URLBuilder.() -> Unit) = URLBuilder(host)
    .apply {
        protocol = if (protocol.isSecure()) protocol.copy(name = "https") else protocol.copy(name = "http")
    }
    .apply(block)

private val Node.restClient: HttpClient
    get() {
        val lavakord = this.lavakord as? AbstractLavakord ?: error("Only supported on default implementation")
        return lavakord.restClient
    }

internal val Node.json: Json
    get() {
        val lavakord = this.lavakord as? AbstractLavakord ?: error("Only supported on default implementation")
        return lavakord.json
    }
