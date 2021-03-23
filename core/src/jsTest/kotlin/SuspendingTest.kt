import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

@OptIn(ExperimentalCoroutinesApi::class)
actual object Tests : CoroutineScope by GlobalScope {
    actual fun Tests.runBlocking(block: suspend () -> Unit): dynamic = promise { block() }
}
