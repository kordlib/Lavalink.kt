package dev.schlaubi.lavakord.rest.models

import dev.schlaubi.lavakord.audio.player.Equalizer
import dev.schlaubi.lavakord.audio.player.Filters
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@PublishedApi
internal data class FiltersObject(
    override val equalizers: MutableList<Equalizer> = mutableListOf(),
    override var volume: Float? = null,
    override var karaoke: Karaoke? = null,
    override var timescale: Timescale? = null,
    override var tremolo: Tremolo? = null,
    override var vibrato: Vibrato? = null,
    override var rotation: Rotation? = null,
    override var distortion: Distortion? = null,
    override var channelMix: ChannelMix? = null,
    override var lowPass: LowPass? = null,
    override val pluginFilters: MutableMap<String, JsonElement> = mutableMapOf()
) : Filters {

    override fun reset() {
        super.reset()
        karaoke = null
        timescale = null
        tremolo = null
        vibrato = null
        rotation = null
        distortion = null
        channelMix = null
        lowPass = null
    }

    override fun unsetKaraoke() {
        karaoke = null
    }

    override fun unsetTimescale() {
        timescale = null
    }

    override fun unsetTremolo() {
        tremolo = null
    }

    override fun unsetVibrato() {
        vibrato = null
    }

    override fun unsetRotation() {
        rotation = null
    }

    override fun unsetDistortion() {
        distortion = null
    }

    override fun unsetChannelMix() {
        channelMix = null
    }

    override fun unsetLowPass() {
        lowPass = null
    }

    @Serializable
    data class Karaoke(
        override var level: Float,
        override var monoLevel: Float,
        override var filterBand: Float,
        override var filterWidth: Float
    ) : Filters.Karaoke {
        constructor() : this(
            1F,
            1F,
            220F,
            100F
        )

        override fun reset() {
            level = 1F
            monoLevel = 1F
            filterBand = 220F
            filterWidth = 100F
        }
    }

    class Timescale(
        speed: Double,
        pitch: Double,
        rate: Double
    ) : Filters.Timescale {
        constructor() : this(1.0, 1.0, 1.0)

        override var speed: Double = speed
            set(value) {
                require(value > 0) { "Speed must be greater than 0" }
                field = value
            }

        override var pitch: Double = pitch
            set(value) {
                require(value > 0) { "Pitch must be greater than 0" }
                field = value
            }

        override var rate: Double = rate
            set(value) {
                require(value > 0) { "Rate must be greater than 0" }
                field = value
            }

        override fun reset() {
            speed = 1.0
            pitch = 1.0
            rate = 1.0
        }
    }

    class Tremolo(
        frequency: Float,
        depth: Float
    ) : Filters.Tremolo {
        constructor() : this(2F, .5F)

        override var frequency: Float = frequency
            set(value) {
                require(value > 0) { "Frequency must be greater than 0" }
                field = value
            }

        override var depth: Float = depth
            set(value) {
                require(value > 0 && value <= 1) { "Frequency must be between 0 and 1" }
                field = value
            }

        override fun reset() {
            frequency = 2F
            depth = .5F
        }
    }

    class Vibrato(
        frequency: Float,
        depth: Float
    ) : Filters.Vibrato {
        constructor() : this(2F, .5F)

        override var frequency: Float = frequency
            set(value) {
                require(value > 0 && value <= 14) { "Frequency must be between 0 and 14" }
                field = value
            }

        override var depth: Float = depth
            set(value) {
                require(value > 0 && value <= 1) { "Frequency must be between 0 and 1" }
                field = value
            }

        override fun reset() {
            frequency = 2F
            depth = .5F
        }
    }

    @Serializable
    data class Rotation(override var rotationHz: Double) : Filters.Rotation {
        constructor() : this(0.0)

        override fun reset() {
            rotationHz = 0.0
        }
    }

    @Serializable
    data class Distortion(
        override var sinOffset: Float,
        override var sinScale: Float,
        override var cosOffset: Float,
        override var cosScale: Float,
        override var tanOffset: Float,
        override var tanScale: Float,
        override var offset: Float,
        override var scale: Float
    ) : Filters.Distortion {
        constructor() : this(0f, 1f, 0f, 1f, 0f, 1f, 0f, 1f)

        override fun reset() {
            sinOffset = 0f
            sinScale = 1f
            cosOffset = 0f
            cosScale = 1f
            tanOffset = 0f
            tanScale = 1f
            offset = 0f
            scale = 1f
        }
    }

    @Serializable
    data class ChannelMix(
        override var leftToLeft: Float,
        override var leftToRight: Float,
        override var rightToLeft: Float,
        override var rightToRight: Float
    ) : Filters.ChannelMix {
        constructor() : this(1f, 0f, 0f, 1f)

        override fun reset() {
            leftToLeft = 1f
            leftToRight = 0f
            rightToLeft = 0f
            rightToRight = 1f
        }
    }

    @Serializable
    data class LowPass(override var smoothing: Float) : Filters.LowPass {
        constructor() : this(20.0f)

        override fun reset() {
            smoothing = 20.0f
        }
    }
}
