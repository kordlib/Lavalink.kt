@file:Suppress("unused", "KDocMissingDocumentation")
@file:AutoWired

package me.schlaubi.lavakord.example

import dev.kord.x.commands.annotation.AutoWired
import dev.kord.x.commands.argument.extension.inRange
import dev.kord.x.commands.argument.extension.map
import dev.kord.x.commands.argument.extension.withDefault
import dev.kord.x.commands.argument.primitive.BooleanArgument
import dev.kord.x.commands.argument.primitive.DoubleArgument
import dev.kord.x.commands.argument.primitive.IntArgument
import dev.kord.x.commands.argument.text.StringArgument
import dev.kord.x.commands.kord.bot
import dev.kord.x.commands.kord.model.context.KordCommandEvent
import dev.kord.x.commands.kord.model.prefix.kord
import dev.kord.x.commands.kord.model.prefix.mention
import dev.kord.x.commands.kord.module.module
import dev.kord.x.commands.model.command.invoke
import dev.kord.x.commands.model.module.ModuleModifier
import dev.kord.x.commands.model.prefix.PrefixConfiguration
import dev.kord.x.commands.model.prefix.literal
import dev.kord.x.commands.model.prefix.or
import dev.kord.x.commands.model.prefix.prefix
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.player.*
import dev.schlaubi.lavakord.kord.connectAudio
import dev.schlaubi.lavakord.kord.getLink
import dev.schlaubi.lavakord.kord.lavakord
import dev.schlaubi.lavakord.rest.TrackResponse
import dev.schlaubi.lavakord.rest.loadItem
import kapt.kotlin.generated.configure
import kotlin.time.ExperimentalTime

lateinit var lavalink: LavaKord

suspend fun main(): Unit = bot(System.getenv("token")) {
    configure()
    lavalink = kord.lavakord {
        link {
            autoReconnect = false
        }
    }
    lavalink.addNode("ws://localhost:8080", "youshallnotpass")
}

val prefix: PrefixConfiguration = prefix {
    kord { literal("!") or mention() }
}

val KordCommandEvent.link: Link
    get() = guild?.let { lavalink.getLink(it.id) } ?: error("Missing guild")

val KordCommandEvent.player: Player
    get() = link.player

@OptIn(ExperimentalTime::class, FiltersApi::class)
fun testModule(): ModuleModifier = module("music-test") {
    command("connectAudio") {
        invoke {
            val voiceState = author.asMember((guild ?: return@invoke).id).getVoiceState()

            val channelId = voiceState.channelId
            if (channelId == null) {
                respond("Please connectAudio to a voice channel")
                return@invoke
            }

            link.connectAudio(channelId)
        }
    }

    command("leave") {
        invoke {
            if (link.state == Link.State.CONNECTED) {
                link.disconnectAudio()
            } else {
                respond("Not connected to a channel")
            }
        }
    }

    command("play") {
        invoke(StringArgument) { query ->
            val search = if (query.startsWith("http")) {
                query
            } else {
                "ytsearch:$query"
            }

            if (link.state != Link.State.CONNECTED) {
                respond("Not connectAudio to VC!")
                return@invoke
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

    command("pause") {
        invoke(BooleanArgument.withDefault(true)) { paused ->
            player.pause(!paused)
        }
    }

    command("stop") {
        invoke {
            player.stopTrack()
        }
    }

    command("volume") {
        invoke(IntArgument) { volume ->
            player.setVolume(volume)
        }
    }

    command("seek") {
        invoke(IntArgument) { rawInput ->
            val input = rawInput.toLong() * 1000

            val track = player.playingTrack
            if (track == null) {
                respond("Not playing anything")
                return@invoke
            }

            val newPosition = player.position + input
            if (newPosition < 0 || newPosition > track.length.inMilliseconds.toLong()) {
                message.channel.createMessage("Position is out of bounds")
                return@invoke
            }

            player.seekTo(newPosition)
        }
    }

    command("eq") {
        invoke(
            IntArgument.inRange(1..15).map { it - 1 },
            DoubleArgument.map { it.toFloat() }.inRange(-0.25F..0.25F)
        ) { band, gain ->
            player.applyEqualizer {
                band(band) gain gain

                // you can also do
//              2 gain 0.25F
            }
        }
    }

    command("speed") {
        invoke(IntArgument) { speed ->
            val float = speed.toFloat()
            player.applyFilters {
                timescale {
                    this.speed = float
                }
            }

        }
    }

    command("karaoke") {
        invoke {
            player.applyFilters {
                karaoke {
                    level = 5F
                }
            }
        }
    }
}
