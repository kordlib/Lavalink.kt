package dev.kord.extensions.lavalink.audio.player

import dev.kord.extensions.lavalink.audio.internal.GatewayPayload
import me.schlaubi.lavakord.checkImplementation
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The default gain of all bands
 */
public const val DEFAULT_GAIN: Float = 0F

/**
 * Builder for equalizer settings.
 *
 * ```kotlin
 * player.applyEqualizer {
 *  band(3) gain 0.25
 * }
 * ```
 */
public class EqualizerBuilder internal constructor(private val guildId: Long) {
    internal val bands = mutableListOf<GatewayPayload.EqualizerCommand.Band>()

    /**
     * Resets all bands configured in this builder.
     */
    public fun reset() {
        bands.clear()
    }

    /**
     * Gives you a [BandConfigurator] to configure this band.
     *
     * @see BandConfigurator.gain
     */
    public fun band(band: Int): BandConfigurator {
        require(band in 0..14) { "There are 15 bands (0-14)" }
        bands.removeIf { it.band == band }
        return BandConfigurator(band, this)
    }

    /**
     * Sets the gain of the band with this int as it's id to [gain].
     *
     * @throws IllegalArgumentException if [gain] is not in -0.25..1
     */
    public infix fun Int.gain(gain: Float): Unit = band(this).gain(gain)


    /**
     * Configurator class for band gains.
     *
     * @see BandConfigurator.gain
     */
    public class BandConfigurator internal constructor(internal val id: Int, internal val builder: EqualizerBuilder)

    internal fun toPayload() = GatewayPayload.EqualizerCommand(guildId.toString(), bands)
}

/**
 * Resets the gain of this band to default.
 */
public fun EqualizerBuilder.BandConfigurator.reset() {
    gain(DEFAULT_GAIN)
}

/**
 * Sets the gain of this band to [gain].
 *
 * @throws IllegalArgumentException if [gain] is not in -0.25..1
 */
public infix fun EqualizerBuilder.BandConfigurator.gain(gain: Float) {
    require(gain in -.25F..1F) { "Gain needs to be between -0.25 (muted) and 1. 0 = normal; 0.25 = double" }
    builder.bands += GatewayPayload.EqualizerCommand.Band(id, gain)
}

/**
 * Resets all Bands of this player.
 */
public suspend fun Player.resetEqualizer() {
    applyEqualizer { reset() }
}

/**
 * Applies the [EqualizerBuilder] to this player.
 *
 * @see EqualizerBuilder
 * @see EqualizerBuilder.BandConfigurator
 * @see EqualizerBuilder.BandConfigurator.gain
 */
@OptIn(ExperimentalContracts::class)
public suspend fun Player.applyEqualizer(builder: EqualizerBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    checkImplementation()
    equalizerBuilder.apply(builder)
    node.send(equalizerBuilder.toPayload())
}
