package dev.schlaubi.lavakord.audio.player

import kotlinx.serialization.Serializable
import kotlin.contracts.ExperimentalContracts

/**
 * The default gain of all bands
 */
public const val DEFAULT_GAIN: Float = 0F

/**
 * There are 15 bands (0-14) that can be changed.
 *
 * @property band the number of the band
 * @property gain is the multiplier for the given band. The default value is 0. Valid values range from -0.25 to 1.0,
 *              where -0.25 means the given band is completely muted, and 0.25 means it is doubled. Modifying the gain could
 *              also change the volume of the output.
 */
@Serializable
public data class Band(val band: Int, val gain: Float)


/**
 * Builder for equalizer settings.
 *
 * ```kotlin
 * player.applyFilters {
 *  band(3) gain 0.25
 * }
 * ```
 */
public interface EqualizerBuilder {
    /**
     * The bands to modify.
     */
    public val bands: MutableList<Band>

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
        bands.removeAll { it.band == band }
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
    builder.bands.add(Band(id, gain))
}

/**
 * Resets all Bands of this player.
 */
@Deprecated(
    "Replaced by filters api",
    ReplaceWith("applyFilters { bands.clear() }", "dev.schlaubi.lavakord.audio.player.applyFilters")
)
public suspend fun Player.resetEqualizer() {
    applyFilters { bands.clear() }
}

/**
 * Applies the [EqualizerBuilder] to this player.
 *
 * @see EqualizerBuilder
 * @see EqualizerBuilder.BandConfigurator
 * @see EqualizerBuilder.BandConfigurator.gain
 */
@OptIn(ExperimentalContracts::class)
@Deprecated(
    "Replaced by filters api",
    ReplaceWith("applyFilters(builder)", "dev.schlaubi.lavakord.audio.player.applyFilters")
)
public suspend fun Player.applyEqualizer(builder: EqualizerBuilder.() -> Unit) {
    applyFilters {
        builder()
    }
}
