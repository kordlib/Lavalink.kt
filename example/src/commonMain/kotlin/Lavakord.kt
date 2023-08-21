@file:Suppress("unused", "KDocMissingDocumentation")

package dev.schlaubi.lavakord

import dev.arbjerg.lavalink.protocol.v4.LoadResult
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.response.FollowupPermittingInteractionResponseBehavior
import dev.kord.core.behavior.interaction.response.createEphemeralFollowup
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.string
import dev.schlaubi.lavakord.audio.Event
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.on
import dev.schlaubi.lavakord.kord.getLink
import dev.schlaubi.lavakord.kord.lavakord
import dev.schlaubi.lavakord.plugins.lavasrc.LavaSrc
import dev.schlaubi.lavakord.plugins.sponsorblock.Sponsorblock
import dev.schlaubi.lavakord.plugins.sponsorblock.model.Category
import dev.schlaubi.lavakord.plugins.sponsorblock.rest.putSponsorblockCategories
import dev.schlaubi.lavakord.rest.loadItem

lateinit var lavalink: LavaKord

expect fun getEnv(name: String): String?

@OptIn(PrivilegedIntent::class)
suspend fun main() {
    val kord = Kord(getEnv("token") ?: error("Missing token"))
    kord.createGlobalApplicationCommands {
        input("connect", "Connects to your channel")
        input("pause", "Pauses the player")
        input("stop", "Stops the player")
        input("leave", "Leaves the channel")
        input("play", "Starts playing a track") {
            string("query", "The query you want to play")
        }
    }

    val listenedGuilds = mutableListOf<Snowflake>()
    lavalink = kord.lavakord {
        plugins {
            install(LavaSrc)
            install(Sponsorblock)
        }
    }

    lavalink.addNode("ws://localhost:2333", "youshallnotpass")

    kord.on<GuildChatInputCommandInteractionCreateEvent> {
        val ack = interaction.deferPublicResponse()
        val link = lavalink.getLink(interaction.guildId)
        val player = link.player
        player.putSponsorblockCategories(Category.MusicOfftopic)
        if (interaction.guildId !in listenedGuilds) {
            val followUpCreator = FollowupPermittingInteractionResponseBehavior(
                interaction.applicationId, interaction.token, interaction.kord, interaction.supplier
            )
            player.on<Event> {
                try {
                    followUpCreator.createEphemeralFollowup { content = "Event: ${this@on}" }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            listenedGuilds.add(interaction.guildId)
        }

        when (interaction.command.rootName) {
            "connect" -> {
                val voiceState = this.interaction.user.asMember(interaction.guildId).getVoiceState()

                val channelId = voiceState.channelId
                if (channelId == null) {
                    ack.respond { content = "Please connectAudio to a voice channel" }
                    return@on
                }

                link.connectAudio(channelId.value)
            }

            "pause" -> player.pause(!player.paused)
            "stop" -> player.stopTrack()
            "leave" -> link.destroy()
            "play" -> {
                val query = interaction.command.options["query"]?.value.toString()
                val search = if (query.startsWith("http")) {
                    query
                } else {
                    "ytsearch:$query"
                }

                if (link.state != Link.State.CONNECTED) {
                    ack.respond { content = "Not connectAudio to VC!" }
                    return@on
                }

                when (val item = link.loadItem(search)) {
                    is LoadResult.TrackLoaded -> player.playTrack(track = item.data)
                    is LoadResult.PlaylistLoaded -> player.playTrack(track = item.data.tracks.first())
                    is LoadResult.SearchResult -> player.playTrack(item.data.tracks.first())
                    is LoadResult.NoMatches -> ack.respond { content = "No matches" }
                    is LoadResult.LoadFailed -> ack.respond { content = item.data.message ?: "Exception" }
                }
            }
        }
    }

    kord.login {
        intents += Intent.MessageContent
    }
}
