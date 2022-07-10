package dev.schlaubi.lavakord.dispers

import dev.bitflow.dispers.client.DispersClient
import dev.schlaubi.lavakord.LavaKord
import dev.schlaubi.lavakord.LavaKordOptions
import dev.schlaubi.lavakord.MutableLavaKordOptions
import kotlin.properties.Delegates

/**
 * Creates a [LavaKord] instance for this [Kord] instance.
 *
 * @param configure a receiver configuring the [LavaKordOptions] instance used for configuration of this instance
 */
public fun DispersClient.lavakord(configure: DispersLavaKordOptions.() -> Unit): LavaKord {
    val dispersOpt = DispersLavaKordOptions().apply(configure)
    val options = MutableLavaKordOptions().apply(dispersOpt.lavaKordOptions).seal()
    return DispersLavaKord(
        this,
        dispersOpt.botId,
        dispersOpt.botShards,
        options
    )
}

public class DispersLavaKordOptions {
    public var botId: ULong by Delegates.notNull()
    public var botShards: Int by Delegates.notNull()
    public val lavaKordOptions: MutableLavaKordOptions.() -> Unit = {}
}
