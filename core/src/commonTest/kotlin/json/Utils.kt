package json

import dev.schlaubi.lavakord.audio.internal.GatewayModule
import dev.schlaubi.lavakord.audio.internal.GatewayPayload
import dev.schlaubi.lavakord.rest.RoutePlannerModule
import dev.schlaubi.lavakord.rest.RoutePlannerStatus
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import json.src.GUILD_ID
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.jvm.JvmName
import kotlin.test.assertEquals
import kotlin.test.assertTrue

infix fun <T> T?.shouldBe(that: T?): Unit =
    assertEquals(this, that, "${this?.toString()} was expected to be ${that?.toString()}")

operator fun <T> T.invoke(block: T.() -> Unit): Unit = run(block)

internal fun <T : GatewayPayload> T.check(checker: T.() -> Unit) {
    if (this !is GatewayPayload.StatsEvent) {
        guildId shouldBe GUILD_ID.toString()
    }
    checker(this)
}


val gatewayJson = Json {
    classDiscriminator = "op"

    serializersModule = GatewayModule
}

val restJson = Json {
    serializersModule = RoutePlannerModule
    classDiscriminator = "class"
    ignoreUnknownKeys = true
}

@JvmName("testGatewayPayload")
internal inline fun <reified T : GatewayPayload> test(input: String, crossinline checker: T.() -> Unit = {}) =
    test<GatewayPayload, T>(
        gatewayJson, input, checker
    )

fun MockRequestHandleScope.respondJson(json: String) =
    respond(json, HttpStatusCode.OK, headersOf("Content-Type" to listOf("application/json")))


@JvmName("testRoutePlannerStatus")
internal inline fun <reified T : RoutePlannerStatus<out RoutePlannerStatus.Data>> test(
    input: String,
    crossinline checker: T.() -> Unit
) =
    testRest<RoutePlannerStatus<out RoutePlannerStatus.Data>, T>(input, checker)

internal inline fun <reified B, reified T : B> testRest(input: String, crossinline checker: T.() -> Unit = {}) = test(
    restJson, input, checker
)

private inline fun <reified B, reified T : B> test(json: Json, input: String, crossinline checker: T.() -> Unit = {}) {
    val command = json.decodeFromString<B>(input)
    assertTrue(command is T, "Unexpected json instance: $command expected: ${T::class.simpleName}")
    checker(command)
    val jsonCommand = json.encodeToString<B>(command)
    val reParsed = json.decodeFromString<B>(jsonCommand)
    assertEquals(command, reParsed, "Reserialized command has to be the same")
}
