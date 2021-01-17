import dev.kord.x.lavalink.LavaKord
import dev.kord.x.lavalink.LavaKordOptions
import dev.kord.x.lavalink.MutableLavaKordOptions
import dev.kord.x.lavalink.audio.Link
import dev.kord.x.lavalink.audio.Node
import dev.kord.x.lavalink.audio.internal.AbstractLavakord
import dev.kord.x.lavalink.audio.internal.AbstractLink
import dev.kord.x.lavalink.rest.loadItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel
import kotlin.coroutines.CoroutineContext
import kotlin.time.ExperimentalTime
import kotlin.time.minutes
import kotlin.time.seconds

// There is no Kotlin/JS Discord wrapper yet (See https://github.com/kordlib/kord/issues/69)
// so this is just to test websocket connectivity and rest.
@OptIn(ExperimentalTime::class)
suspend fun main() {

    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.TRACE

    val lavakord: LavaKord = DummyLavakord(0L, 0, MutableLavaKordOptions().seal())
    lavakord.addNode("wss://lavakord.eu.ngrok.io", "youshallnotpass")
    val link = lavakord.getLink(0)

    // Normally you would need to wait for your DAPI wrapper to connect to discord
    // Plus connect to a voice channel and shit
    // So Lavakord most certainly had connected a node by then
    // However we would try to send a websocket packet before the connection established here
    // so we just wait for 10 seconds  until it is there
    delay(10.seconds)

    // This doesn't do anything since we can't actually connect to a VC due to no API wrapper
    // But it is an easy way of sending a websocket packet
    link.player.setVolume(100)

    println(link.loadItem("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))

    // Normally your Discord API wrapper would run some sort of blocking operation on the main thread but
    // as kord does not support js rn and the lavalink nodes run on separate threads we will just delay this one for ever
    while (true) {
        delay(1.minutes)
    }

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
