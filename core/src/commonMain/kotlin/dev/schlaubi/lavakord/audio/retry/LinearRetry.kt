package dev.schlaubi.lavakord.audio.retry

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.coroutines.delay
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Duration

private val LOG = KotlinLogging.logger { }

/**
 * A linear [Retry] strategy.
 * @property firstBackoff the delay for the first try
 * @property maxBackoff the max delay
 * @property maxTries the maximal amount of tries before giving up
 */
internal class LinearRetry(
    private val firstBackoff: Duration,
    private val maxBackoff: Duration,
    private val maxTries: Int
) : Retry {

    init {
        require(firstBackoff.isPositive()) { "firstBackoff needs to be positive but was ${firstBackoff.inWholeMilliseconds} ms" }
        require(maxBackoff.isPositive()) { "maxBackoff needs to be positive but was ${maxBackoff.inWholeMilliseconds} ms" }
        require(
            maxBackoff.minus(firstBackoff).isPositive()
        ) { "maxBackoff ${maxBackoff.inWholeMilliseconds} ms needs to be bigger than firstBackoff ${firstBackoff.inWholeMilliseconds} ms" }
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

        // tries/maxTries ratio * (backOffDiff) = retryProgress
        val retryProgress =
            (tries.incrementAndGet() / maxTries) * (maxBackoff.inWholeMilliseconds - firstBackoff.inWholeMilliseconds)
        val diff = firstBackoff.inWholeMilliseconds + retryProgress

        LOG.info { "retry attempt ${tries.value}/$maxTries, delaying for $diff ms." }
        delay(diff)
    }
}
