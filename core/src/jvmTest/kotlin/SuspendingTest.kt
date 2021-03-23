import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope

@OptIn(ExperimentalCoroutinesApi::class)
actual object Tests : CoroutineScope by TestCoroutineScope() {
    // Wait for https://github.com/Kotlin/kotlinx.coroutines/issues/1204 and use runBlockingTest
    // This would speed up tests for retry strategies because it skips delays
    actual fun Tests.runBlocking(block: suspend () -> Unit): Unit = kotlinx.coroutines.runBlocking { block() }
}
