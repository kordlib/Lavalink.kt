@file:Suppress("KDocMissingDocumentation")

import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.player.*
import dev.schlaubi.lavakord.rest.TrackResponse
import dev.schlaubi.lavakord.rest.loadItem
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

// There is no Kotlin/JS Discord wrapper yet (See https://github.com/kordlib/kord/issues/69)
// so this is just to test websocket connectivity and rest.
@OptIn(ExperimentalTime::class)
suspend fun main() {

    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.TRACE

    val client = Discord.Client()

    val token = process.env["TOKEN"] ?: error("""Please define an "TOKEN" env variable""")

    client.on("ready") {
        println("Logged in as ${client.user.tag}")
        GlobalScope.launch {
            startLavakord(client)
        }
    }

    client.login(token).await()

    // Normally your Discord API wrapper would run some sort of blocking operation on the main thread but
    // as kord does not support js rn and the lavalink nodes run on separate threads we will just delay this one for ever
    while (true) {
        delay(1.minutes)
    }
}

@OptIn(ExperimentalTime::class, FiltersApi::class)
private suspend fun commandHandler(message: Discord.Message, lavakord: dev.schlaubi.lavakord.LavaKord, client: Discord.Client) {
    val input = message.content
    val (command, args) = with(input.split("\\s+".toRegex())) { first() to drop(1) }
    val guild = message.guild
    val channel = message.channel
    val link = lavakord.getLink(guild.id)
    val player = link.player
    when (command) {
        "!ragequit" -> client.destroy()
        "!restart" -> {
            client.destroy()
            client.login(process.env["TOKEN"] ?: return)
        }
        "!connect" -> {
            val author = message.author
            val voiceState = guild.voiceStates.resolve(author.id)
            if (voiceState == null) {
                channel.send("Please connect to VC!")
                return
            }
            val channelId = voiceState.channelID?.toLong() ?: return

            link.connectAudio(channelId)
        }
        "!pause" -> {
            player.pause(!player.paused)
        }

        "!stop" -> {
            player.stopTrack()
        }
        "!leave" -> {
            link.destroy()
        }
        "!volume" -> {
            val volume = args[0].toInt()
            player.setVolume(volume)
        }
        "!seek" -> {
            val long = args[0].toLong() * 1000
            val track = player.playingTrack
            if (track == null) {
                message.channel.send("Not playing anything")
                return
            }
            val newPosition = player.position + long
            if (newPosition < 0 || newPosition > track.length.inMilliseconds.toLong()) {
                message.channel.send("Position is out of bounds")
                return
            }
            player.seekTo(newPosition)
        }
        "!eq" -> {
            val band = args[0].toInt()
            val gain = args[1].toFloat()

            player.applyEqualizer {
                band(band) gain gain

                // you can also do
                //2 gain 1F
            }
        }
        "!speed" -> {
            val float = args[0].toFloat()
            player.applyFilters {
                timescale {
                    speed = float
                }
            }
        }
        "!karaoke" -> {
            player.applyFilters {
                karaoke {
                    level = 5F
                }
            }
        }
        "!play" -> {
            val query = args.drop(1).joinToString(" ")
            val search = if (query.startsWith("http")) {
                query
            } else {
                "ytsearch:$query"
            }

            if (link.state != Link.State.CONNECTED) {
                message.channel.send("Not connectAudio to VC!")
                return
            }

            val item = link.loadItem(search)

            when (item.loadType) {
                TrackResponse.LoadType.TRACK_LOADED -> player.playTrack(item.tracks.first())
                TrackResponse.LoadType.PLAYLIST_LOADED -> player.playTrack(item.tracks.first())
                TrackResponse.LoadType.SEARCH_RESULT -> player.playTrack(item.tracks.first())
                TrackResponse.LoadType.NO_MATCHES -> message.channel.send("No matches")
                TrackResponse.LoadType.LOAD_FAILED -> message.channel.send(
                    item.exception?.message ?: "Error"
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
suspend fun startLavakord(client: Discord.Client) {
    val lavakord: dev.schlaubi.lavakord.LavaKord =
        client.lavakord { } // In an ideal world we would be able to use Kord here, but we can't see discord-js(-lavakord).kt
    lavakord.addNode("wss://lavakord.eu.ngrok.io", "youshallnotpass")

    client.on("message") {
        GlobalScope.launch {
            commandHandler(it as Discord.Message, lavakord, client)
        }
    }
}
