package me.schlaubi.lavakord.kord

import dev.kord.core.Kord
import me.schlaubi.lavakord.LavaKord
import me.schlaubi.lavakord.audio.PenaltyProvider
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Creates a [LavaKord] instance for this [Kord] instance.
 *
 * @param configure a receiver configuring the [LavaKordOptions] instance used for configuration of this instance
 */
@Suppress("unused")
@Deprecated("This is being renamed in favor of new LavaKord api", ReplaceWith("lavakord"))
public fun Kord.lavalink(configure: MutableLavaKordOptions.() -> Unit = {}): LavaKord = lavakord(configure)

/**
 * Creates a [LavaKord] instance for this [Kord] instance.
 *
 * @param configure a receiver configuring the [LavaKordOptions] instance used for configuration of this instance
 */
public fun Kord.lavakord(configure: MutableLavaKordOptions.() -> Unit = {}): LavaKord {
    val options = MutableLavaKordOptions().apply(configure).seal()
    return KordLavaKord(
        this,
        this.selfId.value,
        resources.shardCount,
        options
    )
}

/**
 * Interface representing options for Kordlink.
 *
 * @property loadBalancer configuration for the load balancer
 * @property link configuration for links between lavalink and guilds
 */
public interface LavaKordOptions {

    public val loadBalancer: LoadBalancingConfig
    public val link: LinkConfig

    /**
     * Configuration for the load balancer.
     *
     * @property penaltyProviders list of penalty providers
     */
    public interface LoadBalancingConfig {
        public val penaltyProviders: List<PenaltyProvider>

        /**
         * Adds a new penalty provider.
         */
        public operator fun plus(provider: PenaltyProvider)
    }

    /**
     * Configuration for Links and Nodes.
     *
     * @property autoReconnect Whether to auto-reconnect links or not
     * @property resumeTimeout amount of seconds Lavalink will wait to kill all players if the client fails to resume it's connection
     * @property maxReconnectTries maximal amount of tries to reconnect to a node
     */
    public interface LinkConfig {
        public val autoReconnect: Boolean
        public val resumeTimeout: Int
        public val maxReconnectTries: Int
    }
}

/**
 * Mutable implementation of [LavaKordOptions].
 *
 * @property loadBalancer [LavaKordOptions.LoadBalancingConfig]
 * @property link [LavaKordOptions.LinkConfig]
 */
public data class MutableLavaKordOptions(
    override val loadBalancer: LoadBalancingConfig = LoadBalancingConfig(),
    override val link: LinkConfig = LinkConfig()
) : LavaKordOptions {

    /**
     * Applies [block] to [loadBalancer].
     */
    @OptIn(ExperimentalContracts::class)
    public fun loadBalancing(block: LoadBalancingConfig.() -> Unit): LoadBalancingConfig {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        return loadBalancer.apply(block)
    }

    /**
     * Applies [block] to [link].
     */
    @OptIn(ExperimentalContracts::class)
    public fun link(block: LinkConfig.() -> Unit): LinkConfig {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        return link.apply(block)
    }

    internal fun seal() = ImmutableLavaKordOptions(loadBalancer.seal(), link.seal())

    /**
     * Mutable implementation of [LavaKordOptions.LoadBalancingConfig].
     */
    public data class LoadBalancingConfig(override val penaltyProviders: MutableList<PenaltyProvider> = mutableListOf()) :
        MutableList<PenaltyProvider> by penaltyProviders,
        LavaKordOptions.LoadBalancingConfig {
        override fun plus(provider: PenaltyProvider) {
            penaltyProviders.plus(provider)
        }

        internal fun seal() = ImmutableLavaKordOptions.LoadBalancingConfig(penaltyProviders.toList())
    }

    /**
     * Mutable implementation of [LavaKordOptions.LinkConfig].
     */
    public data class LinkConfig(
        override var autoReconnect: Boolean = true,
        override var resumeTimeout: Int = 60,
        override var maxReconnectTries: Int = 5
    ) : LavaKordOptions.LinkConfig {
        internal fun seal() = ImmutableLavaKordOptions.LinkConfig(autoReconnect, resumeTimeout, maxReconnectTries)
    }
}

internal data class ImmutableLavaKordOptions(
    override val loadBalancer: LoadBalancingConfig,
    override val link: LinkConfig
) : LavaKordOptions {

    internal data class LoadBalancingConfig(override val penaltyProviders: List<PenaltyProvider>) :
        LavaKordOptions.LoadBalancingConfig {
        override fun plus(provider: PenaltyProvider) =
            throw UnsupportedOperationException("This config has been sealed")
    }

    internal data class LinkConfig(
        override val autoReconnect: Boolean,
        override val resumeTimeout: Int,
        override val maxReconnectTries: Int
    ) : LavaKordOptions.LinkConfig
}
