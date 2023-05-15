@file:Suppress("unused", "KDocMissingDocumentation")

package me.schlaubi.lavakord.example

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.on
import dev.schlaubi.lavakord.kord.lavakord
import dev.schlaubi.lavakord.rest.loadItem
import dev.schlaubi.lavakord.rest.models.TrackResponse

lateinit var lavalink: LavaKord

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    val kord = Kord(System.getenv("token"))
    val listenedGuilds = mutableListOf<Snowflake>()
    lavalink = kord.lavakord()

    lavalink.addNode("wss://schlaubi.eu.ngrok.io", "youshallnotpass")

    kord.on<MessageCreateEvent> {
        val args = message.content.split(" ")

        val link = lavalink.getLink(guildId?.toString() ?: return@on)
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
                    TrackResponse.LoadType.TRACK_LOADED,
                    TrackResponse.LoadType.PLAYLIST_LOADED,
                    TrackResponse.LoadType.SEARCH_RESULT -> player.playTrack(
                        item.tracks.first()
                    )

                    TrackResponse.LoadType.NO_MATCHES -> message.channel.createMessage("No matches")
                    TrackResponse.LoadType.LOAD_FAILED -> message.channel.createMessage(
                        item.exception?.message ?: "Exception"
                    )
                }
            }
        }
    }

    kord.login {
        intents += Intent.MessageContent
    }
}
