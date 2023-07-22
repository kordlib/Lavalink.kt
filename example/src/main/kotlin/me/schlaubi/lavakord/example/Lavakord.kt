@file:Suppress("unused", "KDocMissingDocumentation")

package me.schlaubi.lavakord.example

import dev.arbjerg.lavalink.protocol.v4.LoadResult
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
import dev.schlaubi.lavakord.plugins.lavasrc.LavaSrc
import dev.schlaubi.lavakord.plugins.sponsorblock.Sponsorblock
import dev.schlaubi.lavakord.plugins.sponsorblock.model.Category
import dev.schlaubi.lavakord.plugins.sponsorblock.rest.putSponsorblockCategories
import dev.schlaubi.lavakord.rest.loadItem

lateinit var lavalink: LavaKord

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    val kord = Kord(System.getenv("token"))
    val listenedGuilds = mutableListOf<Snowflake>()
    lavalink = kord.lavakord {
        plugins {
            install(LavaSrc)
            install(Sponsorblock)
        }
    }

    lavalink.addNode("ws://localhost:2333", "youshallnotpass")

    kord.on<MessageCreateEvent> {
        val args = message.content.split(" ")

        val link = lavalink.getLink(guildId?.toString() ?: return@on)
        val player = link.player
        player.putSponsorblockCategories(Category.MusicOfftopic)
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

                when (val item = link.loadItem(search)) {
                    is LoadResult.TrackLoaded -> {
                        player.playTrack(track = item.data)
                    }

                    is LoadResult.PlaylistLoaded -> {
                        player.playTrack(track = item.data.tracks.first())
                    }

                    is LoadResult.SearchResult -> player.playTrack(
                        item.data.tracks.first()
                    )

                    is LoadResult.NoMatches -> message.channel.createMessage("No matches")
                    is LoadResult.LoadFailed -> message.channel.createMessage(
                        item.data.message ?: "Exception"
                    )

                    else -> {
                        error("Unknown result: $item")
                    }
                }
            }
        }
    }

    kord.login {
        intents += Intent.MessageContent
    }
}
