package me.schlaubi.lavakord.rest

import kotlinx.serialization.Serializable

@Serializable
data class RoutePlannerStatus(
    val `class`: String,
    val data: Data
) {
    @Serializable
    sealed interface Data {
        val ipBlock: IpBlock
        val failingAddresses: List<FailingAddress>
        val blockIndex: Long
        val currentAddressIndex: Long

        @Serializable
        data class IpBlock(
            val type: String,
            val size: Long
        )

        @Serializable
        data class FailingAddress(
            val address: String,
            val failingTimestamp: Long,
            val failingTime: String
        )
    }
}

@Serializable
@SerialName("RotatingIpRoutePlanner")
data class RotatingIpRoutePlannerData(
    override val ipBlock: IpBlock,
    override val failingAddresses: List<FailingAddress>,
    override val blockIndex: Long,
    override val currentAddressIndex: Long,
    val rotateIndex: String,
    val ipIndex: String,
    val currentAddress: String
) : RoutePlannerStatus.Data

@Serializable
@SerialName("NanoIpRoutePlanner")
data class NanoIpRoutePlannerData(
    override val ipBlock: IpBlock,
    override val failingAddresses: List<FailingAddress>,
    override val blockIndex: Long,
    override val currentAddressIndex: Long,
    val currentAddressIndex: Long,
) : RoutePlannerStatus.Data

@Serializable
@SerialName("RotatingNanoIpRoutePlanner")
data class RotatingNanoIpRoutePlannerData(
    override val ipBlock: IpBlock,
    override val failingAddresses: List<FailingAddress>,
    override val blockIndex: Long,
    override val currentAddressIndex: Long,
    val blockIndex: String,
    val currentAddressIndex: Long,
) : RoutePlannerStatus.Data
