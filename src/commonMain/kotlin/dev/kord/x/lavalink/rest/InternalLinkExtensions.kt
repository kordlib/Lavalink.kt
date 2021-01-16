package dev.kord.x.lavalink.rest

import dev.kord.x.lavalink.audio.Node
import dev.kord.x.lavalink.audio.internal.AbstractLavakord
import dev.kord.x.lavalink.audio.internal.NodeImpl
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import mu.KotlinLogging
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

internal val LOG = KotlinLogging.logger {}

internal suspend inline fun <reified T> Node.get(noinline urlBuilder: URLBuilder.() -> Unit): T =
    restClient.get(buildUrl(urlBuilder).build()) { addHeader(this@get) }

internal suspend inline fun <reified T> Node.post(
    urlBuilder: URLBuilder,
    block: HttpRequestBuilder.() -> Unit
) =
    restClient.get<T>(urlBuilder.build()) { addHeader(this@post); block(this) }

private fun HttpRequestBuilder.addHeader(socket: Node) {
    headers["Authorization"] = socket.authenticationHeader
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
