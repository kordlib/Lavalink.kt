import dev.kord.extensions.lavalink.LavaKord
import dev.kord.extensions.lavalink.LavaKordOptions
import dev.kord.extensions.lavalink.MutableLavaKordOptions
import dev.kord.extensions.lavalink.audio.Link
import dev.kord.extensions.lavalink.audio.Node
import dev.kord.extensions.lavalink.audio.internal.AbstractLavakord
import dev.kord.extensions.lavalink.audio.internal.AbstractLink
import dev.kord.extensions.lavalink.rest.loadItem
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

// There is no Kotlin/JS Discord wrapper yet (See https://github.com/kordlib/kord/issues/69)
// so this is just to test websocket connectivity and rest.
suspend fun main() {
    val lavakord: LavaKord = DummyLavakord(0L, 0, MutableLavaKordOptions().seal())
    lavakord.addNode("wss://staging-automator-cloud.ngrok.io", "youshallnotpass")
    val link = lavakord.getLink(0)

    println(link.loadItem("https://youtu.be/dQw4w9WgXcQ"))
}

private class DummyLavakord(userId: Long, shardsTotal: Int, options: LavaKordOptions) : AbstractLavakord(
    userId, shardsTotal,
    options
) {
    override fun buildNewLink(guildId: Long, node: Node): Link = DummyLink(node, guildId, this)

    override val coroutineContext: CoroutineContext = Dispatchers.Default + Job()
}

private class DummyLink(node: Node, guildId: Long, override val lavakord: AbstractLavakord) :
    AbstractLink(node, guildId) {
    override suspend fun connectAudio(voiceChannelId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun disconnectAudio() {
        TODO("Not yet implemented")
    }
}
