package dev.kord.x.jda

import dev.kord.x.lavalink.LavaKord

public fun LavaKord.getShardIdForGuild(snowflake: Long): Int = ((snowflake shr 22) % shardsTotal).toInt()
