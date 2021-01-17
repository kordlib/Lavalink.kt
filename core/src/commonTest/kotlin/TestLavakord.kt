import dev.kord.x.lavalink.MutableLavaKordOptions
import dev.kord.x.lavalink.audio.Link
import dev.kord.x.lavalink.audio.Node
import dev.kord.x.lavalink.audio.internal.AbstractLavakord
import dev.kord.x.lavalink.audio.internal.NodeImpl
import io.ktor.client.engine.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope

const val AUTH_HEADER = "RANDOM_AUTH"

class RestTestLavakord(
    httpClientEngine: HttpClientEngineFactory<HttpClientEngineConfig>,
) : AbstractLavakord(0L, 0, httpClientEngine, MutableLavaKordOptions().seal()), CoroutineScope by Tests {
    override fun buildNewLink(guildId: Long, node: Node): Link {
        throw UnsupportedOperationException()
    }
}

@Suppress("TestFunctionName")
internal fun TestNode(lavakord: AbstractLavakord) = NodeImpl(Url("wss://nothing"), "TestNode", AUTH_HEADER, lavakord)
