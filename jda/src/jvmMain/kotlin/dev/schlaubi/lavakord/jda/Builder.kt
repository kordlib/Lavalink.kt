package dev.schlaubi.lavakord.jda

import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.LavaKordOptions
import dev.schlaubi.lavakord.MutableLavaKordOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.future.await
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import kotlin.coroutines.CoroutineContext

private val LOG = KotlinLogging.logger { }

/**
 * Builds a new [LavaKordJDA] and applies [builder] to it.
 *
 * This adds the required [VoiceDispatchInterceptor] and [EventListener] for Lavakord and calls [JDABuilder.build].
 *
 * @param executor the [CoroutineContext] used to dispatch coroutines for Lavakord I/O (Defaults to [JDA.getGatewayPool])
 *
 * @see LavaKordJDA
 */
public suspend fun JDABuilder.buildWithLavakord(
    executor: CoroutineContext? = null,
    options: MutableLavaKordOptions = MutableLavaKordOptions(),
    builder: LavaKordOptions.() -> Unit = {}
): LJDA {
    val lavaKordJDA = LavaKordJDA()
    applyLavakord(lavaKordJDA)

    build().lavakord(lavaKordJDA, executor, options, builder)

    return lavaKordJDA
}

/**
 * Applies all needed options of this [shardManager] to this [DefaultShardManagerBuilder].
 *
 * @see buildWithLavakord
 */
public fun DefaultShardManagerBuilder.applyLavakord(shardManager: LavaKordShardManager): DefaultShardManagerBuilder =
    apply {
        addEventListeners(shardManager)
        setVoiceDispatchInterceptor(shardManager)
    }

/**
 * Applies all needed options of this [jda] to this [JDABuilder].
 *
 * @see buildWithLavakord
 */
public fun JDABuilder.applyLavakord(jda: LavaKordJDA): JDABuilder =
    apply {
        addEventListeners(jda)
        setVoiceDispatchInterceptor(jda)
    }

/**
 * Builds the [LavaKord] instance for this [ShardManager].
 *
 * Example usage:
 * ```kotlin
 * val lavakordShardManager = LavaKordShardManager()
 * val shardManager = DefaultShardManagerBuilder.createDefault(token)
 *    // you don't need to call this and add "lavakordShardManager" as an event listener and VoiceDispatchInterceptor yourself
 *   .applyLavakord(lavakordShardManager)
 *   .build()
 *
 * val lavakord = shardManager.lavakord(lavakordShardManager)
 */
public suspend fun ShardManager.lavakord(
    shardManager: LavaKordShardManager,
    executor: CoroutineContext? = null,
    options: MutableLavaKordOptions = MutableLavaKordOptions(),
    builder: LavaKordOptions.() -> Unit = {}
): LavaKord {
    shardManager.shardManager = this
    val jdaProvider: (Int) -> JDA =
        { shardId -> getShardById(shardId) ?: error("Could not find shard with id: $shardId") }

    val settings = options.apply(builder).seal()
    val lavakord = JDALavakord(
        jdaProvider,
        executor ?: (Dispatchers.IO + SupervisorJob()),
        retrieveApplicationInfo().submit().await().idLong.toULong(),
        shardsTotal,
        settings
    )
    shardManager.internalLavakord = lavakord
    return lavakord
}

/**
 * Builds the [LavaKord] instance for this [ShardManager].
 *
 * Example usage:
 * ```kotlin
 * val lavakordJDA = LavaKordJDA()
 * val jda = JDABuilder.createDefault(token)
 *    // you don't need to call this and add "LavaKordJDA" as an event listener and VoiceDispatchInterceptor yourself
 *   .applyLavakord(lavakordShardManager)
 *   .build()
 *
 * val lavakord = jda.lavakord(lavakordJDA)
 */
public suspend fun JDA.lavakord(
    jda: LavaKordJDA,
    executor: CoroutineContext? = null,
    options: MutableLavaKordOptions = MutableLavaKordOptions(),
    builder: LavaKordOptions.() -> Unit = {}
): LavaKord {
    if (shardManager != null) {
        LOG.warn { "JDA.lavakord() was called on a shard managed instance, consider using ShardManager.lavakord()" }
    }
    jda.jda = this
    val jdaProvider: (Int) -> JDA = { this }

    val settings = options.apply(builder).seal()
    val lavakord = JDALavakord(
        jdaProvider,
        executor ?: (Dispatchers.IO + SupervisorJob()),
        retrieveApplicationInfo().submit().await().idLong.toULong(),
        1,
        settings
    )
    jda.internalLavakord = lavakord
    return lavakord
}

/**
 * Builds a new [LavaKordShardManager] and applies [builder] to it.
 *
 * This adds the required [VoiceDispatchInterceptor] and [EventListener] for Lavakord and calls [DefaultShardManagerBuilder.build]
 *
 * @param executor the [CoroutineContext] used to dispatch coroutines for Lavakord I/O (Defaults to [Dispatchers.IO])
 *
 * @see LavaKordShardManager
 */
public suspend fun DefaultShardManagerBuilder.buildWithLavakord(
    executor: CoroutineContext? = null,
    options: MutableLavaKordOptions = MutableLavaKordOptions(),
    builder: LavaKordOptions.() -> Unit = {}
): LShardManager {
    val lavakordShardManager = LavaKordShardManager()
    applyLavakord(lavakordShardManager)
    val shardManager = build()

    shardManager.lavakord(lavakordShardManager, executor, options, builder)

    return lavakordShardManager
}
