import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest

@OptIn(ExperimentalCoroutinesApi::class)
actual object Tests : CoroutineScope by TestCoroutineScope() {
    actual fun Tests.runBlocking(block: suspend () -> Unit): Unit = runBlockingTest { block() }
}
