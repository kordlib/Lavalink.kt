package dev.schlaubi.lavakord.interop

import dev.schlaubi.lavakord.audio.EventSource
import kotlinx.coroutines.jdk9.asPublisher
import mu.KotlinLogging
import java.util.concurrent.Flow
import java.util.concurrent.Flow.Subscriber
import java.util.function.Consumer

private val LOG = KotlinLogging.logger { }

/**
 * Java equivalent of [EventSource].
 *
 * @param T the base event type
 *
 * @property suspendingEventSource the underlying delegate [EventSource]
 * @property events All events received using a [Flow.Publisher]
 */
public interface JavaEventSource<T : Any> {
    public val suspendingEventSource: EventSource<T>

    public val events: Flow.Publisher<T>
        get() = suspendingEventSource.events.asPublisher()

    /**
     * Creates an event handler which executes [Consumer] for every event of [eventType].
     */
    public fun <E : T> on(eventType: Class<E>, handler: Consumer<E>) {
        events.subscribe(object : Subscriber<T> {
            override fun onSubscribe(subscription: Flow.Subscription) = Unit

            override fun onError(throwable: Throwable) {
                // This in theory should never happen
                LOG.error(throwable) { "An error occurred whilst subscribing to events" }
            }

            override fun onComplete() = Unit

            override fun onNext(item: T) {
                if (eventType.isInstance(item)) {
                    handler.accept(eventType.cast(item))
                }
            }
        })
    }
}

