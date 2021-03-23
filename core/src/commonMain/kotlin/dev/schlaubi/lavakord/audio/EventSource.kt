package dev.schlaubi.lavakord.audio

import dev.schlaubi.lavakord.LavaKord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * A source of events of type [T].
 *
 * @property events a [Flow] of events
 * @property coroutineScope the default [CoroutineScope] to launch listeners in
 *
 * @see EventSource.on
 */
public interface EventSource<T> {

    public val events: Flow<T>
    public val coroutineScope: CoroutineScope

}

/**
 * Convenience method that will invoke the [consumer] on every event [T] created by [EventSource.events].
 *
 * The events are buffered in an [unlimited][Channel.UNLIMITED] [buffer][Flow.buffer] and
 * [launched][CoroutineScope.launch] in the supplied [scope], which is [LavaKord] by default.
 * Each event will be [launched][CoroutineScope.launch] inside the [scope] separately and
 * any thrown [Throwable] will be caught and logged.
 *
 * The returned [Job] is a reference to the created coroutine, call [Job.cancel] to cancel the processing of any further
 * events.
 */
public inline fun <T, reified E : T> EventSource<T>.on(
    scope: CoroutineScope = coroutineScope,
    noinline consumer: suspend E.() -> Unit
): Job =
    events.buffer(Channel.UNLIMITED).filterIsInstance<E>()
        .onEach {
            scope.launch {
                consumer(it)
            }
        }
        .launchIn(scope)
