package dev.kord.extensions.lavalink.kord

import dev.kord.core.Kord
import dev.kord.extensions.lavalink.LavaKord
import dev.kord.extensions.lavalink.LavaKordOptions
import dev.kord.extensions.lavalink.MutableLavaKordOptions
import dev.kord.extensions.lavalink.audio.internal.PenaltyProvider

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
