package dev.schlaubi.lavakord.jda

import dev.schlaubi.lavakord.LavaKord
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor
import net.dv8tion.jda.api.sharding.ShardManager

/**
 * Response of Lavakord builders
 *
 * @property lavakord The [LavaKord] that got built
 */
public interface LavakordJdaBase {
    public val lavakord: LavaKord
}

/**
 * Result of a JDA Lavakord builder.
 *
 * @property jda the [JDA] instance that got built
 */
public interface LJDA : LavakordJdaBase {
    public val jda: JDA
}

/**
 * Result of a ShardManager Lavakord builder.
 *
 * @property shardManager the [ShardManager] instance that got built
 */
public interface LShardManager : LavakordJdaBase {
    public val shardManager: ShardManager
}

/**
 * Internal class.
 */
public sealed class AbstractLavakordJda : VoiceDispatchInterceptor, EventListener, LavakordJdaBase {
    /**
     * The [LavaKord] instance that for this [JDA]/[ShardManager].
     */
    override val lavakord: LavaKord get() = internalLavakord ?: error("Lavakord has not been initialized yet")
    internal var internalLavakord: JDALavakord? = null

    /**
     * @see EventListener.onEvent
     */
    override fun onEvent(event: GenericEvent): Unit = internalLavakord?.onEvent(event) ?: Unit

    /**
     * @see VoiceDispatchInterceptor.onVoiceServerUpdate
     */
    override fun onVoiceServerUpdate(update: VoiceDispatchInterceptor.VoiceServerUpdate): Unit =
        internalLavakord?.onVoiceServerUpdate(update) ?: Unit

    /**
     * @see VoiceDispatchInterceptor.onVoiceServerUpdate
     */
    override fun onVoiceStateUpdate(update: VoiceDispatchInterceptor.VoiceStateUpdate): Boolean =
        internalLavakord?.onVoiceStateUpdate(update) ?: false
}

/**
 * Bridge between [LavaKord] and [JDA].
 *
 * @see buildWithLavakord
 *
 * @property jda the [JDA] instance that has been built
 */
public class LavaKordJDA : AbstractLavakordJda(), LJDA {
    override lateinit var jda: JDA
}

/**
 * Bridge between [ShardManager] and [JDA].
 *
 * @see buildWithLavakord
 *
 * @property shardManager the [ShardManager] instance that has been built
 */
public class LavaKordShardManager : AbstractLavakordJda(), LShardManager {
    override lateinit var shardManager: ShardManager
}
