package dev.kord.x.jda

import dev.kord.x.lavalink.LavaKord
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.hooks.EventListener
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor
import net.dv8tion.jda.api.sharding.ShardManager

public interface LavakordJdaBase {
    public val lavakord: LavaKord
}

public interface LJDA : LavakordJdaBase {
    public val jda: JDA
}

public interface LShardManager: LavakordJdaBase {
    public val shardManager: ShardManager
}

/**
 * Internal class.
 */
internal sealed class AbstractLavakordJda : VoiceDispatchInterceptor, EventListener, LavakordJdaBase {
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
internal class LavaKordJDA : AbstractLavakordJda(), LJDA {
    override lateinit var jda: JDA
        internal set
}

/**
 * Bridge between [ShardManager] and [JDA].
 *
 * @see buildWithLavakord
 *
 * @property shardManager the [ShardManager] instance that has been built
 */
internal class LavaKordShardManager : AbstractLavakordJda(), LShardManager {
    override lateinit var shardManager: ShardManager
        internal set
}
