package dev.schlaubi.lavakord.rest

import dev.schlaubi.lavakord.audio.RestNode
import dev.schlaubi.lavakord.audio.internal.AbstractLavakord
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.resources.*
import kotlinx.serialization.json.Json

internal suspend inline fun <reified T, reified Resource : Any> RestNode.get(
    resource: Resource,
    builder: HttpRequestBuilder.() -> Unit = {}
): T = request(HttpMethod.Get, resource, builder)

internal suspend inline fun <reified T, reified Resource : Any> RestNode.post(
    resource: Resource,
    block: HttpRequestBuilder.() -> Unit = {}
): T = requestWithBody(HttpMethod.Post, resource, block)

internal suspend inline fun <reified T, reified Resource : Any> RestNode.patch(
    resource: Resource,
    block: HttpRequestBuilder.() -> Unit
): T = requestWithBody(HttpMethod.Patch, resource, block)

internal suspend inline fun <reified T, reified Resource : Any> RestNode.delete(
    resource: Resource
): T = request(HttpMethod.Delete, resource)

internal suspend inline fun <reified T, reified Resource : Any> RestNode.requestWithBody(
    method: HttpMethod,
    resource: Resource,
    builder: HttpRequestBuilder.() -> Unit = {}
): T = request(method, resource) {
    contentType(ContentType.Application.Json)
    builder()
}

internal suspend inline fun <reified T, reified Resource : Any> RestNode.request(
    method: HttpMethod,
    resource: Resource,
    builder: HttpRequestBuilder.() -> Unit = {}
): T {
    val resources = restClient.plugin(Resources)
    val nodeHost = host
    return restClient.request {
        this.method = method
        url {
            takeFrom(nodeHost)
            href(resources.resourcesFormat, resource, this)
            // URL is prefix with WebSocket protocol
            protocol = if (protocol.isSecure()) URLProtocol.HTTPS else URLProtocol.HTTP
        }

        header(HttpHeaders.Authorization, authenticationHeader)
        accept(ContentType.Application.Json)
        builder()
    }.body()
}

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
