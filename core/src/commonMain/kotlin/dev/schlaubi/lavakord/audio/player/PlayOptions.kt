package dev.schlaubi.lavakord.audio.player

import dev.schlaubi.lavakord.rest.models.FiltersObject
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Options for playing a new track.
 *
 * @property position the position where the track is supposed to start
 * @property end the end of the track (can be used to only play a section of a track.
 * @property volume the player volume from 0 to 1000
 * @property pause whether to pause the player or not
 * @property noReplace whether to replace the current track with the new track.
 * @property filters the new filters to apply. This will override all previously applied filters
 */
public class PlayOptions {
    public var position: Duration? = null

    /**
     * The position where the track is supposed to start in milliseconds
     *
     * @see position
     */
    @Deprecated("Replaced by position", ReplaceWith("position.?inWholeMilliseconds"))
    public var startTime: Long?
        get() = position?.inWholeMilliseconds
        set(value) {
            position = value?.toDuration(DurationUnit.MILLISECONDS)
        }

    /**
     * The position where the track is supposed to end in milliseconds
     *
     * @see end
     */
    @Deprecated("Replaced by position", ReplaceWith("end.?inWholeMilliseconds"))

    public var endTime: Long?
        get() = end?.inWholeMilliseconds
        set(value) {
            end = value?.toDuration(DurationUnit.MILLISECONDS)
        }

    public var end: Duration? = null

    public var volume: Int? = null
        set(value) {
            require(value == null || value in 1..1000) { "Volume needs to be within 1 and 1000   " }
            field = value
        }
    public var noReplace: Boolean? = null
    public var pause: Boolean? = null
    public var filters: Filters? = null

    /**
     * Set's the [Filters] to apply when starting this track.
     *
     * **This will override all existing filters**
     */
    @OptIn(ExperimentalContracts::class)
    public inline fun filters(filters: Filters.() -> Unit) {
        contract {
            callsInPlace(filters, InvocationKind.EXACTLY_ONCE)
        }
        this.filters = FiltersObject().apply(filters)
    }
}
