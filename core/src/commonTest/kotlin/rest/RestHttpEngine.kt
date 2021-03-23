package rest

import AUTH_HEADER
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*

@Suppress("TestFunctionName")
fun RestHttpEngine(configure: MockEngineConfig.() -> Unit): HttpClientEngineFactory<HttpClientEngineConfig> =
    object : HttpClientEngineFactory<HttpClientEngineConfig> {
        override fun create(block: HttpClientEngineConfig.() -> Unit): HttpClientEngine {
            val config: MockEngineConfig.() -> Unit = {
                block(this)
                configure(this)
            }

            return MockEngine.create(config)
        }
    }

inline fun MockRequestHandleScope.checkAuth(data: HttpRequestData, block: () -> HttpResponseData): HttpResponseData {
    return if (data.headers["Authorization"] != AUTH_HEADER) {
        respondError(HttpStatusCode.Unauthorized)
    } else block()
}
