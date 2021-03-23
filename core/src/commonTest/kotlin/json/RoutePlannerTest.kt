package json

import dev.schlaubi.lavakord.rest.NanoIpRoutePlanner
import dev.schlaubi.lavakord.rest.RotatingIpRoutePlanner
import dev.schlaubi.lavakord.rest.RotatingNanoIpRoutePlanner
import dev.schlaubi.lavakord.rest.RoutePlannerStatus
import json.src.NANO_IP_ROUTE_PLANNER
import json.src.ROTATING_IP_ROUTE_PLANNER
import json.src.ROTATING_NANO_IP_ROUTE_PLANNER
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertTrue

fun RoutePlannerStatus.Data.IpBlock.validate() {
    type shouldBe "Inet6Address"
    size shouldBe 1213123321
}

fun List<RoutePlannerStatus.Data.FailingAddress>.validate() {
    assertTrue(size == 1)
    first().run {
        address shouldBe "/1.0.0.0"
        failingTimestamp shouldBe 1573520707545
        failingTime shouldBe "Mon Nov 11 20:05:07 EST 2019"
    }
}

class RoutePlannerTest {

    @JsName("testRotatingNanoIpRoutePlanner")
    @Test
    fun `test rotating nano ip route planner`() {
        test<RotatingNanoIpRoutePlanner>(ROTATING_NANO_IP_ROUTE_PLANNER) {
            `class` shouldBe RoutePlannerStatus.Class.RotatingNanoIpRoutePlanner
            details {
                ipBlock.validate()
                failingAddresses.validate()
                blockIndex shouldBe 0
                currentAddressIndex shouldBe 36792023813
            }
        }
    }

    @JsName("testRotatingIpRoutePlanner")
    @Test
    fun `test rotating ip route planner`() {
        test<RotatingIpRoutePlanner>(ROTATING_IP_ROUTE_PLANNER) {
            `class` shouldBe RoutePlannerStatus.Class.RotatingIpRoutePlanner
            details {
                ipBlock.validate()
                failingAddresses.validate()
                rotateIndex shouldBe "1"
                ipIndex shouldBe "1"
                currentAddress shouldBe "1"
            }
        }
    }

    @JsName("testNanoIpRoutePlanner")
    @Test
    fun `test nano ip route planner`() {
        test<NanoIpRoutePlanner>(NANO_IP_ROUTE_PLANNER) {
            `class` shouldBe RoutePlannerStatus.Class.NanoIpRoutePlanner
            details {
                ipBlock.validate()
                failingAddresses.validate()
                currentAddressIndex shouldBe 1
            }
        }
    }
}
