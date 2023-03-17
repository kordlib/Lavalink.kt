package rest

import RestTestLavakord
import TestNode
import dev.schlaubi.lavakord.RestError
import dev.schlaubi.lavakord.RestException
import dev.schlaubi.lavakord.rest.getInfo
import io.ktor.http.*
import json.respondJson
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ErrorHandlingTest {
    private val error = RestError(
        Clock.System.now(),
        500,
        "Well this is awkward",
        "trace",
        "I just can't help it",
        "/error"
    )
    private val engine = RestHttpEngine {
        addHandler {
            respondJson(Json.encodeToString(error), HttpStatusCode.InternalServerError)
        }
    }

    @Test
    @JsName("test1")
    fun `test error handling`(): TestResult = runTest {
        val lavakord = RestTestLavakord(engine)
        val node = TestNode(lavakord)

        val exception = assertFailsWith<RestException> { node.getInfo() }
        assertEquals(error, exception.error)
    }
}
