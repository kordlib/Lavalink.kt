package me.schlaubi.lavakord.audio

import com.gitlab.kordlib.core.Kord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import lavalink.client.player.LavalinkPlayer
import lavalink.client.player.event.PlayerEvent
import me.schlaubi.lavakord.asKordLink

/**
 * A [Flow] of [PlayerEvent] received from Lavalink.
 */
public val LavalinkPlayer.events: Flow<PlayerEvent>
    get() = link.asKordLink().events

/**
 * Convenience method that will invoke the [consumer] on every event [T] created by [Kord.events].
 *
 * The events are buffered in an [unlimited][Channel.UNLIMITED] [buffer][Flow.buffer] and
 * [launched][CoroutineScope.launch] in the supplied [scope], which is [Kord] by default.
 * Each event will be [launched][CoroutineScope.launch] inside the [scope] separately and
 * any thrown [Throwable] will be caught and logged.
 *
 * The returned [Job] is a reference to the created coroutine, call [Job.cancel] to cancel the processing of any further
 * events.
 */
public inline fun <reified T : PlayerEvent> LavalinkPlayer.on(
    scope: CoroutineScope = this.link.asKordLink().client,
    noinline consumer: suspend T.() -> Unit
): Job =
    events.buffer(Channel.UNLIMITED).filterIsInstance<T>()
        .onEach {
            scope.launch {
                consumer(it)
            }
        }
        .launchIn(scope)

