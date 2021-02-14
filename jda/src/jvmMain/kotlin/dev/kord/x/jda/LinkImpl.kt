package dev.kord.x.jda

import dev.kord.x.lavalink.audio.Node
import dev.kord.x.lavalink.audio.internal.AbstractLink
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild

internal class JDALink(override val lavakord: JDALavakord, node: Node, guildId: Long) : AbstractLink(node, guildId) {

    private val shardId: Int
        get() = lavakord.getShardIdForGuild(guildId)
    private val jda: JDA
        get() = lavakord.jdaProvider(shardId)
    private val guild: Guild
        get() = jda.getGuildById(guildId) ?: error("Could not find guild: $guildId")

    override suspend fun connectAudio(voiceChannelId: Long) {
        val guild = guild
        val channel =
            guild.getVoiceChannelById(voiceChannelId) ?: error("Could not find voice channel: $voiceChannelId")

        jda.directAudioController.connect(channel)
    }

    override suspend fun disconnectAudio() = jda.directAudioController.disconnect(guild)

}
