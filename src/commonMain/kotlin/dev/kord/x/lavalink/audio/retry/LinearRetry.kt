package dev.kord.x.lavalink.audio.retry

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.delay
import mu.KotlinLogging
import kotlin.time.Duration

private val LOG = KotlinLogging.logger { }

/**
 * A linear [Retry] strategy.
 * @property firstBackoff the delay for the first try
 * @property maxBackoff the max delay
 * @property maxTries the maximal amount of tries before giving up
 */
internal class LinearRetry constructor(
    private val firstBackoff: Duration,
    private val maxBackoff: Duration,
    private val maxTries: Int
) : Retry {

    init {
        require(firstBackoff.isPositive()) { "firstBackoff needs to be positive but was ${firstBackoff.toLongMilliseconds()} ms" }
        require(maxBackoff.isPositive()) { "maxBackoff needs to be positive but was ${maxBackoff.toLongMilliseconds()} ms" }
        require(
            maxBackoff.minus(firstBackoff).isPositive()
        ) { "maxBackoff ${maxBackoff.toLongMilliseconds()} ms needs to be bigger than firstBackoff ${firstBackoff.toLongMilliseconds()} ms" }
        require(maxTries > 0) { "maxTries needs to be positive but was $maxTries" }
    }

    private val tries = atomic(0)

    override val hasNext: Boolean
        get() = tries.value < maxTries

    override fun reset() {
        tries.update { 0 }
    }

    override suspend fun retry() {
        if (!hasNext) error("max retries exceeded")

        var diff = (maxBackoff - firstBackoff).toLongMilliseconds() / maxTries
        diff *= tries.incrementAndGet()
        LOG.trace { "retry attempt ${tries.value}, delaying for $diff ms" }
        delay(diff)
    }
}
