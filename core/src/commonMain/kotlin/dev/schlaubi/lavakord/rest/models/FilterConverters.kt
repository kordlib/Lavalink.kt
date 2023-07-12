package dev.schlaubi.lavakord.rest.models

import dev.arbjerg.lavalink.protocol.v4.*
import dev.schlaubi.lavakord.audio.player.Equalizer
import dev.schlaubi.lavakord.audio.player.Filters
import dev.arbjerg.lavalink.protocol.v4.Filters as LavalinkFilters

internal fun Filters.Karaoke.toLavalink() =
    Karaoke(level, monoLevel, filterBand, filterWidth)

internal fun Filters.Timescale.toLavalink() =
    Timescale(speed, pitch, rate)

internal fun Filters.Tremolo.toLavalink() =
    Tremolo(frequency, depth)

internal fun Filters.Vibrato.toLavalink() =
    Vibrato(frequency, depth)

internal fun Filters.Rotation.toLavalink() =
    Rotation(rotationHz)

internal fun Filters.Distortion.toLavalink() =
    Distortion(
        sinOffset,
        sinScale,
        cosOffset,
        cosScale,
        tanOffset,
        tanScale,
        offset,
        scale
    )

internal fun Filters.ChannelMix.toLavalink() =
    ChannelMix(leftToLeft, leftToRight, rightToLeft, rightToRight)

internal fun Filters.LowPass.toLavalink() =
    LowPass(smoothing)

internal fun Equalizer.toLavalink() = Band(band, gain)

internal fun Filters.toLavalink(): LavalinkFilters = LavalinkFilters(
    volume.toOmissible(),
    equalizers.toOmissible(Equalizer::toLavalink),
    karaoke.toOmissible(Filters.Karaoke::toLavalink),
    timescale.toOmissible(Filters.Timescale::toLavalink),
    tremolo.toOmissible(Filters.Tremolo::toLavalink),
    vibrato.toOmissible(Filters.Vibrato::toLavalink),
    distortion.toOmissible(Filters.Distortion::toLavalink),
    rotation.toOmissible(Filters.Rotation::toLavalink),
    channelMix.toOmissible(Filters.ChannelMix::toLavalink),
    lowPass.toOmissible(Filters.LowPass::toLavalink),
    pluginFilters
)

private fun <T, R> T?.toOmissible(map: (T) -> R): Omissible<R> {
    return if (this != null) {
        Omissible.Present(map(this))
    } else {
        Omissible.Omitted()
    }
}

private fun <T, R> Iterable<T>?.toOmissible(map: (T) -> R): Omissible<List<R>> {
    return if (this != null) {
        Omissible.Present(map(map))
    } else {
        Omissible.Omitted()
    }
}
