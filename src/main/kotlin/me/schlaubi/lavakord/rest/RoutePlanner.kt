package me.schlaubi.lavakord.rest

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.decodeStructure

/**
 * Representation of a return value from Lavalink [route planning API](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#routeplanner-api)
 *
 * [class] and [details] can be `null` if no route planner is set
 *
 * @property class the name of the RoutePlanner class
 * @property details the [Data] from the route planner class
 */
@Serializable(with = RoutePlannerStatusSerializer::class)
public data class RoutePlannerStatus(
    val `class`: Class? = null,
    val details: Data? = null
) {

    /**
     * Alias to [class] so you won't need backticks.
     */
    val clazz: Class? by ::`class`

    /**
     * Representation of different RoutePlanner classes
     *
     * See: https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#routeplanner-api
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
 *
 * @property rotateIndex The number of rotations which happened since the restart of Lavalink
 * @property ipIndex The current offset in the block
 * @property currentAddress The currently used ip address
 */
@Suppress("KDocMissingDocumentation") // lavalink doesnt provide
@Serializable
public data class RotatingIpRoutePlannerData(
    override val ipBlock: RoutePlannerStatus.Data.IpBlock,
    override val failingAddresses: List<RoutePlannerStatus.Data.FailingAddress>,
    val rotateIndex: String,
    val ipIndex: String,
    val currentAddress: String
) : RoutePlannerStatus.Data

/**
 * [NanoIpRoutePlanner](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#nanoiprouteplanner)
 *
 * @property currentAddressIndex The current offset in the ip block
 */
@Suppress("KDocMissingDocumentation") // lavalink doesnt provide
@Serializable
public data class NanoIpRoutePlannerData(
    override val ipBlock: RoutePlannerStatus.Data.IpBlock,
    override val failingAddresses: List<RoutePlannerStatus.Data.FailingAddress>,
    val currentAddressIndex: Long,
) : RoutePlannerStatus.Data

/**
 * [RotatingNanoIpRoutePlanner](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#rotatingnanoiprouteplanner)
 *
 * @property blockIndex The information in which /64 block ips are chosen. This number increases on each ban.
 * @property currentAddressIndex The current offset in the ip block
 */
@Suppress("KDocMissingDocumentation") // lavalink doesnt provide
@Serializable
public data class RotatingNanoIpRoutePlannerData(
    override val ipBlock: RoutePlannerStatus.Data.IpBlock,
    override val failingAddresses: List<RoutePlannerStatus.Data.FailingAddress>,
    val blockIndex: Long,
    val currentAddressIndex: Long,
) : RoutePlannerStatus.Data

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = RoutePlannerStatus::class)
internal class RoutePlannerStatusSerializer : KSerializer<RoutePlannerStatus> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("RoutePlannerStatus") {
        element<RoutePlannerStatus.Class>("class")
        element<RoutePlannerStatus.Data>("data")
    }

    override fun deserialize(decoder: Decoder): RoutePlannerStatus {
        return decoder.decodeStructure(descriptor) {
            val clazz = decodeSerializableElement(descriptor, 0, serializer<RoutePlannerStatus.Class>())
            val data = when (clazz) {
                RoutePlannerStatus.Class.RotatingIpRoutePlanner -> decodeSerializableElement(
                    descriptor,
                    1,
                    serializer<RotatingIpRoutePlannerData>()
                )
                RoutePlannerStatus.Class.NanoIpRoutePlanner -> decodeSerializableElement(
                    descriptor,
                    1,
                    serializer<NanoIpRoutePlannerData>()
                )
                RoutePlannerStatus.Class.RotatingNanoIpRoutePlanner -> decodeSerializableElement(
                    descriptor,
                    1,
                    serializer<RotatingNanoIpRoutePlannerData>()
                )
            }
            RoutePlannerStatus(clazz, data)
        }
    }
}
