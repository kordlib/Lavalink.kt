package dev.schlaubi.lavakord.jda

import dev.schlaubi.lavakord.LavaKord

/**
 * Calculates the shard id for the guild with [snowflake] as its id.
 */
public fun LavaKord.getShardIdForGuild(snowflake: ULong): Int =
    ((snowflake shr 22) % shardsTotal.toUInt()).toInt()
