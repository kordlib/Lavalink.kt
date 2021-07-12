import kotlinx.coroutines.*

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
actual object Tests : CoroutineScope by GlobalScope {
    actual fun Tests.runBlocking(block: suspend () -> Unit): dynamic = promise { block() }
}
