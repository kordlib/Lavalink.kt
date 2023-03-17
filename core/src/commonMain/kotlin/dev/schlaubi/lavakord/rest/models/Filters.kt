package dev.schlaubi.lavakord.rest.models

import dev.schlaubi.lavakord.audio.player.Equalizer
import dev.schlaubi.lavakord.audio.player.Filters
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("filters")
@PublishedApi
internal data class FiltersObject(
    @SerialName("equalizer")
    override val equalizers: MutableList<Equalizer> = mutableListOf(),
    override var volume: Float? = null,
    override var karaoke: Karaoke? = null,
    override var timescale: Timescale? = null,
    override var tremolo: Tremolo? = null,
    override var vibrato: Vibrato? = null,
    override var rotation: Rotation? = null,
    override var distortion: Distortion? = null,
    override var channelMix: ChannelMix? = null,
    override var lowPass: LowPass? = null
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

    @Serializable
    data class Timescale(
        @SerialName("speed")
        private var _speed: Float,
        @SerialName("pitch")
        private var _pitch: Float,
        @SerialName("rate")
        private var _rate: Float
    ) : Filters.Timescale {
        constructor() : this(1F, 1F, 1F)

        override var speed: Float
            get() = _speed
            set(value) {
                require(value > 0) { "Speed must be greater than 0" }
                _speed = value
            }

        override var pitch: Float
            get() = _pitch
            set(value) {
                require(value > 0) { "Pitch must be greater than 0" }
                _pitch = value
            }

        override var rate: Float
            get() = _rate
            set(value) {
                require(value > 0) { "Rate must be greater than 0" }
                _rate = value
            }

        override fun reset() {
            _speed = 1F
            _pitch = 1F
            _rate = 1F
        }
    }

    @Serializable
    data class Tremolo(
        @SerialName("frequency") private var _frequency: Float,
        @SerialName("depth") private var _depth: Float
    ) : Filters.Tremolo {
        constructor() : this(2F, .5F)

        override var frequency: Float
            get() = _frequency
            set(value) {
                require(value > 0) { "Frequency must be greater than 0" }
                _frequency = value
            }

        override var depth: Float
            get() = _depth
            set(value) {
                require(value > 0 && value <= 1) { "Frequency must be between 0 and 1" }
                _depth = value
            }

        override fun reset() {
            _frequency = 2F
            _depth = .5F
        }
    }

    @Serializable
    data class Vibrato(
        @SerialName("frequency") private var _frequency: Float,
        @SerialName("depth") private var _depth: Float
    ) : Filters.Vibrato {
        constructor() : this(2F, .5F)

        override var frequency: Float
            get() = _frequency
            set(value) {
                require(value > 0 && value <= 14) { "Frequency must be between 0 and 14" }
                _frequency = value
            }

        override var depth: Float
            get() = _depth
            set(value) {
                require(value > 0 && value <= 1) { "Frequency must be between 0 and 1" }
                _depth = value
            }

        override fun reset() {
            _frequency = 2F
            _depth = .5F
        }
    }

    @Serializable
    data class Rotation(override var rotationHz: Float) : Filters.Rotation {
        constructor() : this(0.0f)

        override fun reset() {
            rotationHz = 0.0f
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
