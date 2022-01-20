@file:JvmName("LavakordBuilder")

package dev.schlaubi.lavakord.interop.jda

import dev.schlaubi.lavakord.LavaKordOptions
import dev.schlaubi.lavakord.MutableLavaKordOptions
import dev.schlaubi.lavakord.jda.LJDA
import dev.schlaubi.lavakord.jda.LShardManager
import dev.schlaubi.lavakord.jda.buildWithLavakord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.future.future
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool
import kotlin.coroutines.CoroutineContext

private object ForkJoinScope : CoroutineScope {
    override val coroutineContext: CoroutineContext = ForkJoinPool.commonPool().asCoroutineDispatcher()
}

/**
 * Builder for JDA lavakord when using Java (For Kotlin please use [buildWithLavakord]).
 *
 * @param jdaBuilder the [JDABuilder] which builds your JDA instance
 * @param options base [MutableLavaKordOptions] (normally you can ignore this)
 */
public class LavakordJDABuilder @JvmOverloads constructor(
    private val jdaBuilder: JDABuilder,
    private val options: MutableLavaKordOptions = MutableLavaKordOptions()
) : LavaKordOptions by options {
    /**
     * [ExecutorService] used for I/O operations (defaults to [JDA.getGatewayPool])
     */
    public var executor: ExecutorService? = null

    /**
     * Builds the JDA and LavaKord instance.
     *
     * @see LJDA
     */
    public fun build(): LJDA = buildAsync().join()

    /**
     * Builds the JDA and LavaKord instance.
     *
     * @see LJDA
     */
    public fun buildAsync(): CompletableFuture<LJDA> =
        ForkJoinScope.future { jdaBuilder.buildWithLavakord(executor?.asCoroutineDispatcher(), options) }
}

/**
 * Builder for JDA ShardManager lavakord when using Java (For Kotlin please use [buildWithLavakord]).
 *
 * @param shardManagerBuilder the [DefaultShardManagerBuilder] which builds your JDA instance
 * @param options base [MutableLavaKordOptions] (normally you can ignore this)
 */
public class LavakordDefaultShardManagerBuilder @JvmOverloads constructor(
    private val shardManagerBuilder: DefaultShardManagerBuilder,
    private val options: MutableLavaKordOptions = MutableLavaKordOptions()
) : LavaKordOptions by options {
    /**
     * [ExecutorService] used for I/O operations (defaults to [IO])
     */
    public var executor: ExecutorService? = null

    /**
     * Builds the ShardManager and LavaKord instance.
     *
     * @see LShardManager
     */
    public fun build(): LShardManager = buildAsync().join()

    /**
     * Builds the ShardManager and LavaKord instance.
     *
     * @see LShardManager
     */
    public fun buildAsync(): CompletableFuture<LShardManager> = ForkJoinScope.future {
        shardManagerBuilder.buildWithLavakord(executor?.asCoroutineDispatcher(), options)
    }
}
