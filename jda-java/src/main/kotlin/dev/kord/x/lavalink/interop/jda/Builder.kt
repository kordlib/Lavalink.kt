@file:JvmName("LavakordBuilder")

package dev.kord.x.lavalink.interop.jda

import dev.kord.x.jda.LJDA
import dev.kord.x.jda.LShardManager
import dev.kord.x.jda.buildWithLavakord
import dev.kord.x.lavalink.LavaKordOptions
import dev.kord.x.lavalink.MutableLavaKordOptions
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.asCoroutineDispatcher
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import java.util.concurrent.ExecutorService

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
    public fun build(): LJDA = jdaBuilder.buildWithLavakord(executor?.asCoroutineDispatcher(), options)
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
    public fun build(): LShardManager =
        shardManagerBuilder.buildWithLavakord(executor?.asCoroutineDispatcher(), options)
}
