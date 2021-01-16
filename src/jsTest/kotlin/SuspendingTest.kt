import kotlinx.coroutines.*

@OptIn(ExperimentalCoroutinesApi::class)
actual object Tests : CoroutineScope by GlobalScope {
    actual fun Tests.runBlocking(block: suspend () -> Unit): dynamic = promise { block() }
}