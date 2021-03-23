package dev.schlaubi.lavakord

import dev.schlaubi.lavakord.audio.internal.PenaltyProvider
import dev.schlaubi.lavakord.audio.retry.LinearRetry
import dev.schlaubi.lavakord.audio.retry.Retry
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.milliseconds
import kotlin.time.seconds


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
     * @property retry retry strategy (See [Retry] and [LinearRetry])
     */
    public interface LinkConfig {
        public val autoReconnect: Boolean
        public val resumeTimeout: Int
        public val retry: Retry
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
    public data class LinkConfig constructor(
        override var autoReconnect: Boolean = true,
        override var resumeTimeout: Int = 60,
        override var retry: Retry = LinearRetry(2.seconds, 60.seconds, 10)
    ) : LavaKordOptions.LinkConfig {
        internal fun seal(): LavaKordOptions.LinkConfig =
            ImmutableLavaKordOptions.LinkConfig(autoReconnect, resumeTimeout, retry)

        /**
         * Creates a linear [Retry] strategy.
         * @property firstBackoff the delay for the first try
         * @property maxBackoff the max delay
         * @property maxTries the maximal amount of tries before giving up
         */
        public fun linear(firstBackoff: Duration, maxBackoff: Duration, maxTries: Int): Retry =
            LinearRetry(firstBackoff, maxBackoff, maxTries)

        /**
         * Creates a linear [Retry] strategy.
         * @property firstBackoff the delay for the first try in ms
         * @property maxBackoff the max delay in ms
         * @property maxTries the maximal amount of tries before giving up
         */
        public fun linear(firstBackoff: Long, maxBackoff: Long, maxTries: Int): Retry =
            LinearRetry(firstBackoff.milliseconds, maxBackoff.milliseconds, maxTries)
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
    data class LoadBalancingConfig(override val penaltyProviders: List<PenaltyProvider>) :
        LavaKordOptions.LoadBalancingConfig {
        override fun plus(provider: PenaltyProvider): Nothing =
            throw UnsupportedOperationException("This config has been sealed")
    }

    /**
     * Mutable implementation of [LavaKordOptions.LinkConfig].
     */
    data class LinkConfig(
        override val autoReconnect: Boolean,
        override val resumeTimeout: Int,
        override val retry: Retry
    ) : LavaKordOptions.LinkConfig
}
