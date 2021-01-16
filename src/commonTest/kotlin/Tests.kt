import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
expect object Tests : CoroutineScope {
    fun Tests.runBlocking(block: suspend () -> Unit)
}
