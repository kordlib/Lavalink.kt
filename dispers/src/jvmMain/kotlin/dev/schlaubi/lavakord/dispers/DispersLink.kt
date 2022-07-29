package dev.schlaubi.lavakord.dispers

import dev.kord.common.entity.*
import dev.kord.core.entity.channel.VoiceChannel
import dev.kord.gateway.UpdateVoiceStatus
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.internal.AbstractLink
import kotlinx.coroutines.flow.count
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

internal class DispersLink(
    guildId: ULong,
    node: Node,
    override val lavakord: DispersLavaKord
) : AbstractLink(node, guildId) {

    override suspend fun connectAudio(voiceChannelId: ULong) {
        lastChannelId = voiceChannelId
        val channel = lavakord.getChannel(guildId, voiceChannelId)
        checkChannel(channel)

        state = Link.State.CONNECTING

        lavakord.sendVoiceUpdate(
            UpdateVoiceStatus(
                Snowflake(guildId),
                channel.id,
                selfMute = false,
                selfDeaf = false
            )
        )
    }

    @OptIn(ExperimentalContracts::class)
    private suspend fun checkChannel(channel: DiscordChannel?) {
        contract {
            returns() implies (channel != null)
        }

        requireNotNull(channel) { "channelId must be the valid id of a voice channel" }
        require(channel.guildId.value == Snowflake(guildId)) {
            "The provided VoiceChannel is not a part of the Guild that this AudioManager handles. Please provide a VoiceChannel from the proper Guild"
        }
/*
        // todo: implement permission checking
        val permissions = channel.getEffectivePermissions(lavakord.kord.selfId)
        if (!permissions.contains(Permission.Connect) && !permissions.contains(Permission.MoveMembers)) {
            throw dev.schlaubi.lavakord.InsufficientPermissionException(
                Permissions(
                    Permission.Connect,
                    Permission.MoveMembers
                ).code.binary
            )
        }

        val voiceState = lavakord.getVoiceState(guildId, lavakord.userId)
        if (channel.id == voiceState?.channelId) return

        if (voiceState?.channelId != null) {
            val limit = channel.userLimit.orElse(0)
            if (!permissions.contains(Permission.Administrator)) {
                if (limit > 0
                    && limit >= channel.voiceStates.count()
                    && !permissions.contains(Permission.MoveMembers)
                )
                    throw dev.schlaubi.lavakord.InsufficientPermissionException(Permissions(Permission.MoveMembers).code.binary)
            }
        }*/
    }

    override suspend fun disconnectAudio() {
        state = Link.State.DISCONNECTING
        lavakord.getGuild(guildId) ?: error("Could not find Guild $guildId")
        lastChannelId = null

        val command = UpdateVoiceStatus(
            Snowflake(guildId),
            null,
            selfMute = false,
            selfDeaf = false
        )
        lavakord.sendVoiceUpdate(command)
    }
}
