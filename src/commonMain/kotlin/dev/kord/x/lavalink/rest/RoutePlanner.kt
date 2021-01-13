package dev.kord.x.lavalink.rest

import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import dev.kord.x.lavalink.rest.RoutePlannerStatus.Data

internal val RoutePlannerModule = SerializersModule {
    polymorphic(RoutePlannerStatus::class) {
        subclass(RotatingIpRoutePlanner::class, RotatingIpRoutePlanner.serializer())
        subclass(NanoIpRoutePlanner::class, NanoIpRoutePlanner.serializer())
        subclass(RotatingNanoIpRoutePlanner::class, RotatingNanoIpRoutePlanner.serializer())
    }
}

/**
 * Representation of a return value from Lavalink [route planning API](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#routeplanner-api)
 *
 * [class] and [details] can be `null` if no route planner is set
 *
 * @property class the name of the RoutePlanner class
 * @property details the [Data] from the route planner class
 */
@Serializable
public sealed class RoutePlannerStatus<T : Data> {

    public abstract val details: Data

    @Suppress("MemberVisibilityCanBePrivate")
    public val `class`: Class
        get() = when (this) {
            is RotatingIpRoutePlanner -> Class.RotatingIpRoutePlanner
            is NanoIpRoutePlanner -> Class.NanoIpRoutePlanner
            is RotatingNanoIpRoutePlanner -> Class.RotatingNanoIpRoutePlanner
        }

    /**
     * Alias to [class] so you won't need backticks.
     */
    public val clazz: Class? by ::`class`

    /**
     * Representation of different RoutePlanner classes
     *
     * See: [https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#routeplanner-api]
     */
    @Suppress("KDocMissingDocumentation") // lavalink doesnt provide
    @Serializable
    public enum class Class {
        RotatingIpRoutePlanner,
        NanoIpRoutePlanner,
        RotatingNanoIpRoutePlanner
    }

    /**
     * Representation of route planner data.
     */
    @Suppress("KDocMissingDocumentation") // lavalink doesnt provide
    public interface Data {
        public val ipBlock: IpBlock
        public val failingAddresses: List<FailingAddress>

        @Serializable
        public data class IpBlock(
            val type: String,
            val size: Long
        )

        @Serializable
        public data class FailingAddress(
            val address: String,
            val failingTimestamp: Long,
            val failingTime: String
        )
    }
}

/**
 * [RotatingIpRoutePlanner](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#rotatingiprouteplanner)
 */
@Suppress("KDocMissingDocumentation") // lavalink doesnt provide
@Serializable
public data class RotatingIpRoutePlanner(override val details: RoutePlannerStatus.Data) :
    RoutePlannerStatus<Data>() {
    /**
     * @property rotateIndex The number of rotations which happened since the restart of Lavalink
     * @property ipIndex The current offset in the block
     * @property currentAddress The currently used ip address
     */
    public data class Data(
        override val ipBlock: RoutePlannerStatus.Data.IpBlock,
        override val failingAddresses: List<RoutePlannerStatus.Data.FailingAddress>,
        public val rotateIndex: String,
        public val ipIndex: String,
        public val currentAddress: String
    ) : RoutePlannerStatus.Data
}

/**
 * [NanoIpRoutePlanner](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#nanoiprouteplanner)
 *
 */
@Suppress("KDocMissingDocumentation") // lavalink doesnt provide
@Serializable
public data class NanoIpRoutePlanner(override val details: RoutePlannerStatus.Data) :
    RoutePlannerStatus<Data>() {
    /**
     * @property currentAddressIndex The current offset in the ip block
     */
    public data class Data(
        override val ipBlock: RoutePlannerStatus.Data.IpBlock,
        override val failingAddresses: List<RoutePlannerStatus.Data.FailingAddress>,
        val currentAddressIndex: Long,
    ) : RoutePlannerStatus.Data
}

/**
 * [RotatingNanoIpRoutePlanner](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#rotatingnanoiprouteplanner)
 */
@Suppress("KDocMissingDocumentation") // lavalink doesnt provide
@Serializable
public data class RotatingNanoIpRoutePlanner(override val details: RoutePlannerStatus.Data) :
    RoutePlannerStatus<Data>() {
    /**
     * @property blockIndex The information in which /64 block ips are chosen. This number increases on each ban.
     * @property currentAddressIndex The current offset in the ip block
     */
    public data class Data(
        override val ipBlock: RoutePlannerStatus.Data.IpBlock,
        override val failingAddresses: List<RoutePlannerStatus.Data.FailingAddress>,
        val blockIndex: Long,
        val currentAddressIndex: Long,
    ) : RoutePlannerStatus.Data
}
