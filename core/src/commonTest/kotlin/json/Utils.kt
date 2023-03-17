package json

import dev.schlaubi.lavakord.audio.internal.GatewayModule
import dev.schlaubi.lavakord.audio.internal.GatewayPayload
import dev.schlaubi.lavakord.rest.RoutePlannerModule
import dev.schlaubi.lavakord.rest.RoutePlannerStatus
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import json.src.GUILD_ID
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import kotlin.jvm.JvmName
import kotlin.test.assertEquals
import kotlin.test.assertTrue

infix fun <T> T?.shouldBe(that: T?): Unit =
    assertEquals(this, that, "${this?.toString()} was expected to be ${that?.toString()}")

operator fun <T> T.invoke(block: T.() -> Unit): Unit = run(block)

internal fun <T : GatewayPayload> T.check(checker: T.() -> Unit) {
    if (this is GatewayPayload.GuildAware) {
        guildId shouldBe GUILD_ID.toString()
    }
    checker(this)
}


val json: Json = Json {
    serializersModule = GatewayModule + RoutePlannerModule
}

fun MockRequestHandleScope.respondJson(json: String, status: HttpStatusCode = HttpStatusCode.OK): HttpResponseData =
    respond(json, status, headersOf("Content-Type" to listOf(ContentType.Application.Json.toString())))

@JvmName("testGatewayPayload")
internal inline fun <reified T : GatewayPayload> testPayload(input: String, crossinline checker: T.() -> Unit = {}) =
    test<GatewayPayload, T>(input, checker)

@JvmName("testAny")
internal inline fun <reified T : Any> test(input: String, crossinline checker: T.() -> Unit = {}) =
    test<T, T>(input, checker)

@JvmName("testRoutePlannerStatus")
internal inline fun <reified T : RoutePlannerStatus<out RoutePlannerStatus.Data>> testPlanner(
    input: String,
    crossinline checker: T.() -> Unit
) = test<RoutePlannerStatus<out RoutePlannerStatus.Data>, T>(input, checker)

private inline fun <reified B, reified T : B> test(input: String, crossinline checker: T.() -> Unit = {}) {
    val command = json.decodeFromString<B>(input)
    assertTrue(command is T, "Unexpected json instance: $command expected: ${T::class.simpleName}")
    checker(command)
    val jsonCommand = json.encodeToString<B>(command)
    val reParsed = json.decodeFromString<B>(jsonCommand)
    assertEquals(command, reParsed, "Re-serialized command has to be the same")
}
