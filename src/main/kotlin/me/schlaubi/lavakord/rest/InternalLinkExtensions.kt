package me.schlaubi.lavakord.rest

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.request.*
import io.ktor.http.*
import lavalink.client.io.LavalinkSocket
import me.schlaubi.lavakord.audio.KordLink

internal val client = HttpClient {
    install(JsonFeature)
}

internal suspend inline fun <reified T> LavalinkSocket.get(urlBuilder: URLBuilder) =
    client.get<T>(urlBuilder.build()) { addHeader(this@get) }

internal suspend inline fun <reified T> LavalinkSocket.post(
    urlBuilder: URLBuilder,
    block: HttpRequestBuilder.() -> Unit
) =
    client.get<T>(urlBuilder.build()) { addHeader(this@post); block(this) }

private fun HttpRequestBuilder.addHeader(socket: LavalinkSocket) {
    headers["Authorization"] = socket.password
}

internal fun LavalinkSocket.buildUrl(block: URLBuilder.() -> Unit) = URLBuilder(remoteUri.toString())
    .apply {
        protocol = if (protocol.isSecure()) protocol.copy(name = "https") else protocol.copy(name = "http")
    }
    .apply(block)

internal val LavalinkSocket.password: String
    get() = (this.javaClass.superclass.getDeclaredField("headers").apply {
        isAccessible = true
    }
        .get(this) as Map<*, *>)["Authorization"] as? String
        ?: error("Could not get password for node")
