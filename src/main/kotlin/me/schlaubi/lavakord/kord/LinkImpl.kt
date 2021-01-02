package me.schlaubi.lavakord.kord

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.gateway.UpdateVoiceStatus
import kotlinx.coroutines.flow.count
import me.schlaubi.lavakord.InsufficientPermissionException
import me.schlaubi.lavakord.audio.Link
import me.schlaubi.lavakord.audio.Node
import me.schlaubi.lavakord.audio.impl.AbstractLink
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

internal class KordLink(
    guildId: Long,
    node: Node,
    override val lavakord: KordLavaKord
) : AbstractLink(node, guildId) {

    override suspend fun connectAudio(voiceChannelId: Long) {
        lastChannelId = voiceChannelId
        val channel = lavakord.kord.getChannel(Snowflake(voiceChannelId)) as VoiceChannel?
        checkChannel(channel)
        val guild = channel.getGuild()

        state = Link.State.CONNECTING
        val gateway = guild.gateway
        checkNotNull(gateway) { "Guild gateway not found" }
        gateway.send(
            UpdateVoiceStatus(
                guild.id,
                channel.id,
                selfMute = false,
                selfDeaf = false
            )
        )
    }

    @OptIn(ExperimentalContracts::class)
    private suspend fun checkChannel(channel: VoiceChannel?) {
        contract {
            returns() implies (channel != null)
        }

        requireNotNull(channel) { "channelId must be the valid id of a voice channel" }
        require(channel.guildId.value == guildId) {
            "The provided VoiceChannel is not a part of the Guild that this AudioManager handles. Please provide a VoiceChannel from the proper Guild"
        }
        val permissions = channel.getEffectivePermissions(lavakord.kord.selfId)
        if (!permissions.contains(Permission.Connect) && !permissions.contains(Permission.MoveMembers)) {
            throw InsufficientPermissionException(Permissions(Permission.Connect, Permission.MoveMembers).code.binary)
        }

        val voiceState = channel.getGuild().getMember(lavakord.kord.selfId).getVoiceStateOrNull()
        if (channel.id == voiceState?.channelId) return

        if (voiceState?.channelId != null) {
            val limit = channel.userLimit
            if (!permissions.contains(Permission.Administrator)) {
                if (limit > 0
                    && limit >= channel.voiceStates.count()
                    && !permissions.contains(Permission.MoveMembers)
                )
                    throw InsufficientPermissionException(Permissions(Permission.MoveMembers).code.binary)
            }
        }
    }

    override suspend fun disconnectAudio() {
        error("The kord people broke disconecting from voice channels so you just have to kick the member")
//        state = Link.State.DISCONNECTING
//        val guild = lavakord.kord.getGuild(Snowflake(guildId)) ?: error("Could not find Guild $guildId")
//        val channel = guild.getChannel(Snowflake(lastChannel!!)) as VoiceChannel
//        lastChannel = null
//        // Hope broke the UpdateVoiceStatus command
//        val command = UpdateVoiceStatus(
//            Snowflake(guildId),
//            Snowflake(0),
//            selfMute = false,
//            selfDeaf = false
//        )
//
//        command.javaClass.getDeclaredField("channelId").apply { isAccessible = true }
//            .set(command, null)
//        guild.gateway!!.send(command        )
    }
}
