import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

private val testScope = TestScope()

object Tests : CoroutineScope by testScope {
    fun runBlocking(block: suspend TestScope.() -> Unit): TestResult = runTest(testBody = block)
}
