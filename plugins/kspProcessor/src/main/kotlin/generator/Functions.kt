@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")

package dev.schlaubi.lavakord.ksp.generator

import com.squareup.kotlinpoet.*
import dev.arbjerg.lavalink.protocol.v4.LoadResult
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.player.Player
import dev.schlaubi.lavakord.internal.GenerateQueryHelper
import dev.schlaubi.lavakord.ksp.*


/**
 * Generates this function.
 *
 * ```kotlin
 * /**
 *  * Performs a track search using [{serviceName}]({serviceUrl}).
 *  * {additionalDoc}
 *  */
 * @OptIn(ExperimentalContracts::class)
 * public suspend fun Player.searchAndPlayUsing{serviceName}(
 *     query: String,
 *     playOptionsBuilder: PlayOptions.() -> Unit
 * ) {
 *     contract {
 *         callsInPlace(playOptionsBuilder, InvocationKind.EXACTLY_ONCE)
 *     }
 *     searchAndPlayTrack("{prefix}:$query", playOptionsBuilder)
 * }
 * ```
 */
internal fun GenerateQueryHelper.searchAndPlay(builderName: ClassName): FunSpec {
    val playOptionsBuilder = ParameterSpec
        .builder("playOptionsBuilder", PLAY_OPTIONS_BUILDER)
        .defaultValue("""{}""")
        .build()
    val builderParameterName = "options"

    return generateFunction("${operationName}AndPlayUsing${functionName}", builderParameterName) { context ->
        receiver(Player::class)
        addModifiers(KModifier.SUSPEND)
        addKdoc("""Performs a track search and plays the result using [$serviceName]($serviceWebsite).""".trim())
        addBuilderContract(playOptionsBuilder)
        if (builderOptions.isNotEmpty()) {
            val parameter = ParameterSpec.builder("options", builderName)
                .defaultValue("%T.Default", builderName)
                .build()
            addParameter(parameter)
        }
        addParameter(playOptionsBuilder)
        addStatement("""searchAndPlayTrack(${context.queryString}, %N)""", playOptionsBuilder)
    }
}

/**
 * Generates this function.
 *
 * ```kotlin
 * /**
 *  * Performs a track search using [{serviceName}]({serviceUrl}).
 *  */
 * public suspend fun Node.searchUsing{serivceName}(query: String): LoadResult =
 *     loadItem("{prefix}:$query")
 *```
 */
internal fun GenerateQueryHelper.search(builderName: ClassName): FunSpec {
    val optionsParameterName = "options"
    val builderLambda = LambdaTypeName.get(builderName, returnType = UNIT)
    val builderParameter = ParameterSpec.builder("builder", builderLambda)
        .defaultValue("{}")
        .build()
    return generateFunction("${operationName}Using${functionName}", optionsParameterName) { context ->
        addModifiers(KModifier.SUSPEND)
        receiver(Node::class)
        returns(LoadResult::class)
        addKdoc("""Performs a track search using [$serviceName]($serviceWebsite).""")
        if (builderOptions.isNotEmpty()) {
            addModifiers(KModifier.INLINE)
            addParameter(builderParameter)
            addBuilderContract(builderParameter)
            addStatement("val %L = %T().apply(%N)", optionsParameterName, builderName, builderParameter)
        }
        addStatement("""return %M(${context.queryString})""", loadItem)
    }
}
