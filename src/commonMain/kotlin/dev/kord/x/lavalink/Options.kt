package dev.kord.x.lavalink

import dev.kord.x.lavalink.audio.internal.PenaltyProvider
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


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
     * @see LinkConfig.autoReconnect
     */
    @Deprecated("Use link { autoReconnect = false } instead", ReplaceWith("link"))
    public var autoReconnect: Boolean
        get() = link.autoReconnect
        set(value) {
            when (link) {
                is ImmutableLavaKordOptions.LinkConfig -> throw UnsupportedOperationException("This options object has already been sealed")
                is MutableLavaKordOptions.LinkConfig -> {
                    (link as MutableLavaKordOptions.LinkConfig).autoReconnect = value
                }
                else -> error("Unknown implementation")
            }
        }

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

    /**
     * Makes thi
     */
    public fun seal(): LavaKordOptions = ImmutableLavaKordOptions(loadBalancer.seal(), link.seal())

    /**
     * Mutable implementation of [LavaKordOptions.LoadBalancingConfig].
     */
    public data class LoadBalancingConfig(override val penaltyProviders: MutableList<PenaltyProvider> = mutableListOf()) :
        MutableList<PenaltyProvider> by penaltyProviders,
        LavaKordOptions.LoadBalancingConfig {
        override fun plus(provider: PenaltyProvider) {
            penaltyProviders.plus(provider)
        }

        internal fun seal(): LavaKordOptions.LoadBalancingConfig =
            ImmutableLavaKordOptions.LoadBalancingConfig(penaltyProviders.toList())
    }

    /**
     * Mutable implementation of [LavaKordOptions.LinkConfig].
     */
    public data class LinkConfig(
        override var autoReconnect: Boolean = true,
        override var resumeTimeout: Int = 60,
        override var maxReconnectTries: Int = 5
    ) : LavaKordOptions.LinkConfig {
        internal fun seal(): LavaKordOptions.LinkConfig =
            ImmutableLavaKordOptions.LinkConfig(autoReconnect, resumeTimeout, maxReconnectTries)
    }
}

/**
 * Immutable implementation of [LavaKordOptions].
 */
private data class ImmutableLavaKordOptions(
    override val loadBalancer: LavaKordOptions.LoadBalancingConfig,
    override val link: LavaKordOptions.LinkConfig
) : LavaKordOptions {

    /**
     * Mutable implementation of [LavaKordOptions.LoadBalancingConfig].
     */
    public data class LoadBalancingConfig(override val penaltyProviders: List<PenaltyProvider>) :
        LavaKordOptions.LoadBalancingConfig {
        override fun plus(provider: PenaltyProvider): Nothing =
            throw UnsupportedOperationException("This config has been sealed")
    }

    /**
     * Mutable implementation of [LavaKordOptions.LinkConfig].
     */
    public data class LinkConfig(
        override val autoReconnect: Boolean,
        override val resumeTimeout: Int,
        override val maxReconnectTries: Int
    ) : LavaKordOptions.LinkConfig
}
