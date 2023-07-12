package dev.schlaubi.lavakord

import dev.schlaubi.lavakord.audio.internal.PenaltyProvider
import dev.schlaubi.lavakord.audio.retry.LinearRetry
import dev.schlaubi.lavakord.audio.retry.Retry
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds


/**
 * Interface representing options for Kordlink.
 *
 * @property loadBalancer configuration for the load balancer
 * @property link configuration for links between lavalink and guilds
 */
public interface LavaKordOptions {

    public val loadBalancer: LoadBalancingConfig
    public val link: LinkConfig
    public val plugins: PluginsConfig

    /**
     * Configuration for the load balancer.
     *
     * @property penaltyProviders list of penalty providers
     */
    public interface LoadBalancingConfig {
        public val penaltyProviders: List<PenaltyProvider>
    }

    /**
     * Configuration for Links and Nodes.
     *
     * @property autoReconnect Whether to auto-reconnect links or not
     * @property resumeTimeout amount of seconds Lavalink will wait to kill all players if the client fails to resume it's connection
     * @property retry retry strategy (See [Retry] and [LinearRetry])
     * @property showTrace whether [RestError.trace] should be populated
     */
    public interface LinkConfig {
        public val autoReconnect: Boolean
        public val resumeTimeout: Int
        public val retry: Retry
        public val showTrace: Boolean
    }

    /**
     * Configuration regarding to plugins.
     * @property plugins a list of installed [Plugins][Plugin]
     */
    public interface PluginsConfig {
        public val plugins: List<Plugin>
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
    override val link: LinkConfig = LinkConfig(),
    override val plugins: PluginsConfig = PluginsConfig()
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
     * Makes this configuration immutable.
     */
    public fun seal(): LavaKordOptions = ImmutableLavaKordOptions(loadBalancer.seal(), link.seal(), plugins.seal())

    /**
     * Mutable implementation of [LavaKordOptions.LoadBalancingConfig].
     */
    public data class LoadBalancingConfig(override val penaltyProviders: MutableList<PenaltyProvider> = mutableListOf()) :
        MutableList<PenaltyProvider> by penaltyProviders,
        LavaKordOptions.LoadBalancingConfig {
        /**
         * Adds a new [PenaltyProvider].
         */
        public operator fun PenaltyProvider.unaryPlus() {
            penaltyProviders.plus(this)
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
        override var retry: Retry = LinearRetry(2.seconds, 60.seconds, 10),
        override val showTrace: Boolean = false
    ) : LavaKordOptions.LinkConfig {
        internal fun seal(): LavaKordOptions.LinkConfig =
            ImmutableLavaKordOptions.LinkConfig(autoReconnect, resumeTimeout, retry, showTrace)

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

    /**
     * Configuration for plugins.
     *
     * @property plugins A list of installed [Plugins][Plugin]
     * @see install
     */
    public data class PluginsConfig(
        override var plugins: MutableList<Plugin> = mutableListOf()
    ) : LavaKordOptions.PluginsConfig {
        /**
         * Installs [plugin].
         *
         * @see Plugin
         */
        public fun install(plugin: Plugin) {
            plugins += plugin
        }

        internal fun seal(): LavaKordOptions.PluginsConfig =
            ImmutableLavaKordOptions.PluginsConfig(plugins)
    }
}

/**
 * Immutable implementation of [LavaKordOptions].
 */
private data class ImmutableLavaKordOptions(
    override val loadBalancer: LavaKordOptions.LoadBalancingConfig,
    override val link: LavaKordOptions.LinkConfig,
    override val plugins: LavaKordOptions.PluginsConfig
) : LavaKordOptions {

    /**
     * Immutable implementation of [LavaKordOptions.LoadBalancingConfig].
     */
    data class LoadBalancingConfig(override val penaltyProviders: List<PenaltyProvider>) :
        LavaKordOptions.LoadBalancingConfig

    /**
     * Immutable implementation of [LavaKordOptions.LinkConfig].
     */
    data class LinkConfig(
        override val autoReconnect: Boolean,
        override val resumeTimeout: Int,
        override val retry: Retry,
        override val showTrace: Boolean
    ) : LavaKordOptions.LinkConfig

    /**
     * Immutable implementation of [LavaKordOptions.PluginsConfig]
     */
    data class PluginsConfig(override val plugins: List<Plugin>) :
        LavaKordOptions.PluginsConfig
}
