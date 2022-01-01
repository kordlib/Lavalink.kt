import dev.schlaubi.lavakord.MutableLavaKordOptions
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.internal.AbstractLavakord
import dev.schlaubi.lavakord.audio.internal.NodeImpl
import io.ktor.client.engine.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope

const val AUTH_HEADER: String = "RANDOM_AUTH"

class RestTestLavakord(
    httpClientEngine: HttpClientEngineFactory<HttpClientEngineConfig>,
) : AbstractLavakord(0UL, 0, httpClientEngine, MutableLavaKordOptions().seal()), CoroutineScope by Tests {
    override fun buildNewLink(guildId: ULong, node: Node): Link {
        throw UnsupportedOperationException()
    }
}

@Suppress("TestFunctionName")
internal fun TestNode(lavakord: AbstractLavakord) = NodeImpl(Url("wss://nothing"), "TestNode", AUTH_HEADER, lavakord)
