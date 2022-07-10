package dev.schlaubi.lavakord.dispers

import dev.bitflow.dispers.client.DispersClient
import dev.bitflow.dispers.client.amqp.*
import dev.kord.common.entity.DiscordChannel
import dev.kord.common.entity.DiscordGuild
import dev.kord.common.entity.DiscordVoiceState
import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Reconnect
import dev.kord.gateway.UpdateVoiceStatus
import dev.kord.gateway.VoiceServerUpdate
import dev.kord.gateway.VoiceStateUpdate
import dev.schlaubi.lavakord.LavaKordOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import dev.schlaubi.lavakord.audio.DiscordVoiceServerUpdateData
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.internal.AbstractLavakord
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

internal class DispersLavaKord(
    private val client: DispersClient,
    userId: ULong,
    shardsTotal: Int,
    options: LavaKordOptions
) : AbstractLavakord(userId, shardsTotal, options) {

    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()

    init {


        client.events
            .onEach { LoggerFactory.getLogger(javaClass).debug(it.event.toString()) }
            .launchIn(this)

        client.events
            .filter { it.event is Reconnect }
            .onEach { onReconnect(client) }
            .launchIn(this)

        client.events
            .filter { it.event is VoiceStateUpdate }
            .onEach { handleVoiceStateUpdate(it.event as VoiceStateUpdate) }
            .launchIn(this)

        client.events
            .filter { it.event is VoiceServerUpdate }
            .onEach { handleVoiceServerUpdate(client, it.event as VoiceServerUpdate) }
            .launchIn(this)
    }

    private fun onReconnect(client: DispersClient) {
        launch {
            if (options.link.autoReconnect) {
                linksMap.forEach { (guildId, link) ->
                    val lastChannel = link.lastChannelId
                    if (lastChannel != null && client.amqp.getGuild(Snowflake(userId), 1, Snowflake(guildId)) != null) {
                        try {
                            link.connectAudio(lastChannel)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }


    private suspend fun handleVoiceStateUpdate(event: VoiceStateUpdate) {
        if (userId != event.voiceState.userId.value) return
        val channel = event.voiceState.channelId
        val link = event.voiceState.guildId.value?.let { linksMap[it.value] } ?: return
        require(link is DispersLink)

        // Null channel means disconnected
        if (channel == null) {
            if (link.state != Link.State.DESTROYED) {
                link.onDisconnected()
            }
        } else {
            link.lastChannelId = channel.value
            link.state = Link.State.CONNECTED
        }
    }

    override fun buildNewLink(guildId: ULong, node: Node): Link =
        DispersLink(guildId, node, this)

    private suspend fun handleVoiceServerUpdate(client: DispersClient, event: VoiceServerUpdate) {
        val link = linksMap[event.voiceServerUpdateData.guildId.value] ?: return
        require(link is DispersLink)
        val guildId = event.voiceServerUpdateData.guildId
        val shard = shard(guildId.value)
        val voiceState = client.amqp.getVoiceState(Snowflake(userId), shard, guildId, Snowflake(userId))

        forwardVoiceEvent(
            link,
            guildId.toString(),
            (voiceState ?: return).sessionId,
            DiscordVoiceServerUpdateData(
                event.voiceServerUpdateData.token,
                guildId.toString(),
                event.voiceServerUpdateData.endpoint
            )
        )
    }

    suspend fun sendVoiceUpdate(state: UpdateVoiceStatus) {
        client.amqp.request(
            Snowflake(userId),
            shard(state.guildId.value),
            AmqpRequest.UpdateVoiceState(state)
        )
    }

    suspend fun getChannel(guildId: ULong, channelId: ULong): DiscordChannel? {
        return client.amqp.getChannel(
            Snowflake(userId),
            shard(guildId),
            Snowflake(guildId),
            Snowflake(channelId)
        )
    }

    suspend fun getVoiceState(guildId: ULong, userId: ULong): DiscordVoiceState? {
        return client.amqp.getVoiceState(
            Snowflake(userId),
            shard(guildId),
            Snowflake(guildId),
            Snowflake(userId)
        )
    }

    suspend fun getGuild(guildId: ULong): SnowflakedDiscordGuild? {
        return client.amqp.getGuild(
            Snowflake(userId),
            shard(guildId),
            Snowflake(guildId),
        )
    }

    private fun shard(guildId: ULong): Int {
        return (guildId.shr(22).toLong() % shardsTotal).toInt()
    }
}
