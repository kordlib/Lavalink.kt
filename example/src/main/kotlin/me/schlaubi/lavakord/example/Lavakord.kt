@file:Suppress("unused", "KDocMissingDocumentation")

package me.schlaubi.lavakord.example

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.x.lavalink.audio.Link
import dev.kord.x.lavalink.audio.on
import dev.kord.x.lavalink.LavaKord
import dev.kord.x.lavalink.audio.player.*
import dev.kord.x.lavalink.kord.lavakord
import dev.kord.x.lavalink.rest.TrackResponse
import dev.kord.x.lavalink.rest.loadItem
import kotlin.time.ExperimentalTime

lateinit var lavalink: LavaKord

@OptIn(ExperimentalTime::class, FiltersApi::class)
suspend fun main() {
    val kord = Kord(System.getenv("token"))
    val listenedGuilds = mutableListOf<Snowflake>()
    lavalink = kord.lavakord {

    }

    lavalink.addNode("wss://staging-automator-cloud.ngrok.io", "youshallnotpass")

    kord.on<MessageCreateEvent> {
        val args = message.content.split(" ")

        val link = lavalink.getLink(guildId?.asString ?: return@on)
        val player = link.player
        val guildId = guildId ?: return@on

        if (guildId !in listenedGuilds) {
            player.on {
                message.getChannel().createMessage("Event: $this")
            }
            listenedGuilds.add(guildId)
        }

        when (args[0]) {
            "!connect" -> {
                val voiceState = member?.getVoiceState() ?: return@on

                val channelId = voiceState.channelId
                if (channelId == null) {
                    message.getChannel().createMessage("Please connectAudio to a voice channel")
                    return@on
                }

                link.connectAudio(channelId.value)
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
                val volume = args[1].toInt()
                player.setVolume(volume)
            }
            "!seek" -> {
                val input = args[1].toLong() * 1000
                val track = player.playingTrack
                if (track == null) {
                    message.channel.createMessage("Not playing anything")
                    return@on
                }
                val newPosition = player.position + input
                if (newPosition < 0 || newPosition > track.length.inMilliseconds.toLong()) {
                    message.channel.createMessage("Position is out of bounds")
                    return@on
                }
                player.seekTo(newPosition)
            }
            "!eq" -> {
                val band = args[1].toInt()
                val gain = args[2].toFloat()

                player.applyEqualizer {
                    band(band) gain gain

                    // you can also do
                    2 gain 1F
                }
            }
            "!speed" -> {
                val float = args[1].toFloat()
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
                    message.getChannel().createMessage("Not connectAudio to VC!")
                    return@on
                }

                val item = link.loadItem(search)

                when (item.loadType) {
                    TrackResponse.LoadType.TRACK_LOADED -> player.playTrack(item.tracks.first())
                    TrackResponse.LoadType.PLAYLIST_LOADED -> player.playTrack(item.tracks.first())
                    TrackResponse.LoadType.SEARCH_RESULT -> player.playTrack(item.tracks.first())
                    TrackResponse.LoadType.NO_MATCHES -> message.channel.createMessage("No matches")
                    TrackResponse.LoadType.LOAD_FAILED -> message.channel.createMessage(
                        item.exception?.message ?: "Error"
                    )
                }
            }
        }
    }

    kord.login()
}

//suspend fun main(): Unit = bot(System.getenv("token")) {
////    configure()
//    lavalink = kord.lava {
//        autoReconnect = false
//    }
//    lavalink.addNode(URI.create("ws://localhost:8080"), "youshallnotpass")
//}

//val prefix: PrefixConfiguration = prefix {
//    kord { literal("!") or mention() }
//}

//fun testModule(): ModuleModifier = module("music-test") {
//    command("connectAudio") {
//        invoke {
//            val guild = guild ?: return@invoke
//            val link = guild.getLink(lavalink)
//
//            val voiceState = author.asMember(guild.id).getVoiceState()
//
//            val channelId = voiceState.channelId
//            if (channelId == null) {
//                respond("Please connectAudio to a voice channel")
//                return@invoke
//            }
//
//            link.connect(channelId)
//        }
//    }
//
//    command("leave") {
//        invoke {
//            val guild = guild ?: return@invoke
//            val link = guild.getLink(lavalink)
//
//            if (link.state == Link.State.CONNECTED) {
//                link.disconnect()
//            } else {
//                respond("Not connected to a channel")
//            }
//        }
//    }
//
//    command("play") {
//        invoke(StringArgument) { query ->
//
//            val search = if (query.startsWith("http")) {
//                query
//            } else {
//                "ytsearch:$query"
//            }
//
//            val guild = guild ?: return@invoke
//            val link = guild.getLink(lavalink)
//            if (link.state != Link.State.CONNECTED) {
//                respond("Not connectAudio to VC!")
//                return@invoke
//            }
//
//            val player = link.player
//
//            player.on<TrackStartEvent> {
//                channel.createMessage(track.info.asString())
//            }
//
//            link.loadItem(search, object : AudioLoadResultHandler {
//                override fun trackLoaded(track: AudioTrack) {
//                    player.playTrack(track)
//                }
//
//                override fun playlistLoaded(playlist: AudioPlaylist) {
//                    player.playTrack(playlist.tracks.first())
//                }
//
//                override fun noMatches() {
//                    kord.launch {
//                        respond("No matches")
//                    }
//                }
//
//                override fun loadFailed(exception: FriendlyException?) {
//                    kord.launch {
//                        respond(exception?.message ?: "")
//                    }
//                }
//
//            })
//        }
//    }
//}
