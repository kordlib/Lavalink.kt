package dev.schlaubi.lavakord.interop

import dev.schlaubi.lavakord.audio.player.applyFilters
import dev.schlaubi.lavakord.audio.player.gain
import java.util.concurrent.CompletableFuture
import dev.schlaubi.lavakord.audio.player.EqualizerBuilder as KotlinEqualizerBuilder

/**
 * Builder for Equalizer configuration.
 *
 * Call [apply] to apply it to a [JavaPlayer].
 * This contains the previous configuration from the player. To reset call [reset].
 */
public class EqualizerBuilder(
    private val bands: MutableMap<Int, Float> = mutableMapOf()
) {
    /**
     * Resets all bands.
     */
    public fun reset(): Unit = bands.clear()

    /**
     * Set's the gain of [band] to [gain].
     *
     * @throws IllegalArgumentException if band is not between 0-14 or gain is not between -0.25F and 1F
     */
    public fun setBand(band: Int, gain: Float): EqualizerBuilder {
        require(band in 0..14) { "There are 15 bands (0-14)" }
        require(gain in -.25F..1F) { "Gain needs to be between -0.25 (muted) and 1. 0 = normal; 0.25 = double" }
        bands[band] = gain
        return this
    }

    /**
     * Applies this configuration to a [JavaPlayer].
     */
    public fun apply(player: JavaPlayer): CompletableFuture<Void> {
        val builder: KotlinEqualizerBuilder.() -> Unit = {
            bands.forEach { (band, gain) -> band(band) gain gain }
        }

        return player.run {
            player.suspendingPlayer.applyFilters(builder)
        }
    }
}
