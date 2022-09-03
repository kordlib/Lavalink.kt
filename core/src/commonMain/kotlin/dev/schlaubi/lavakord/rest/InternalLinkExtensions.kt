package dev.schlaubi.lavakord.rest

import dev.schlaubi.lavakord.audio.RestNode
import dev.schlaubi.lavakord.audio.internal.AbstractLavakord
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

internal suspend inline fun <reified T> RestNode.get(noinline urlBuilder: URLBuilder.() -> Unit): T =
    restClient.get(buildUrl(urlBuilder).build()) lambda@{
        addHeader(this@get); accept(ContentType.Application.JavaScript)
    }.body()

internal suspend inline fun <reified T> RestNode.post(
    urlBuilder: URLBuilder,
    block: HttpRequestBuilder.() -> Unit
) =
    restClient.get(urlBuilder.build()) {
        addHeader(this@post); accept(ContentType.Application.JavaScript); block(this)
    }.body<T>()

private fun HttpRequestBuilder.addHeader(socket: RestNode) {
    header(HttpHeaders.Authorization, socket.authenticationHeader)
}

internal fun RestNode.buildUrl(block: URLBuilder.() -> Unit) = URLBuilder(host)
    .apply {
        protocol = if (protocol.isSecure()) protocol.copy(name = "https") else protocol.copy(name = "http")
    }
    .apply(block)

private val RestNode.restClient: HttpClient
    get() {
        val lavakord = this.lavakord as? AbstractLavakord ?: error("Only supported on default implementation")
        return lavakord.restClient
    }

internal val RestNode.json: Json
    get() {
        val lavakord = this.lavakord as? AbstractLavakord ?: error("Only supported on default implementation")
        return lavakord.json
    }
