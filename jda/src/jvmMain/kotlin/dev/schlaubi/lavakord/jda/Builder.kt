package dev.schlaubi.lavakord.jda

import dev.schlaubi.lavakord.LavaKordOptions
import dev.schlaubi.lavakord.MutableLavaKordOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import kotlin.coroutines.CoroutineContext

/**
 * Builds a new [LavaKordJDA] and applies [builder] to it.
 *
 * This adds the required [VoiceDispatchInterceptor] and [EventListener] for Lavakord and calls [JDABuilder.build].
 *
 * @param executor the [CoroutineContext] used to dispatch coroutines for Lavakord I/O (Defaults to [JDA.getGatewayPool])
 *
 * @see LavaKordJDA
 */
public fun JDABuilder.buildWithLavakord(
    executor: CoroutineContext? = null,
    options: MutableLavaKordOptions = MutableLavaKordOptions(),
    builder: LavaKordOptions.() -> Unit = {}
): LJDA {
    val settings = options.apply(builder).seal()
    val lavakordJda = LavaKordJDA()
    addEventListeners(lavakordJda)
    setVoiceDispatchInterceptor(lavakordJda)
    val jda = build()
    val coroutineContext = executor ?: jda.gatewayPool.asCoroutineDispatcher()
    lavakordJda.jda = jda
    val jdaProvider: (Int) -> JDA = { jda }
    val lavakord = JDALavakord(
        jdaProvider,
        coroutineContext,
        jda.selfUser.idLong.toULong(),
        jda.shardInfo.shardTotal,
        settings
    )
    lavakordJda.internalLavakord = lavakord

    return lavakordJda
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
public fun DefaultShardManagerBuilder.buildWithLavakord(
    executor: CoroutineContext? = null,
    options: MutableLavaKordOptions = MutableLavaKordOptions(),
    builder: LavaKordOptions.() -> Unit = {}
): LShardManager {
    val settings = options.apply(builder).seal()
    val lavakordShardManager = LavaKordShardManager()
    addEventListeners(lavakordShardManager)
    setVoiceDispatchInterceptor(lavakordShardManager)
    val shardManager = build()
    lavakordShardManager.shardManager = shardManager
    val jdaProvider: (Int) -> JDA =
        { shardId -> shardManager.getShardById(shardId) ?: error("Could not find shard with id: $shardId") }
    val lavakord = JDALavakord(
        jdaProvider,
        executor ?: (Dispatchers.IO + Job()),
        shardManager.retrieveApplicationInfo().complete().idLong.toULong(),
        shardManager.shardsTotal,
        settings
    )

    lavakordShardManager.internalLavakord = lavakord
    return lavakordShardManager
}
