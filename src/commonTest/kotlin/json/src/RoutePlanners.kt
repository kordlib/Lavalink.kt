package json.src

//language=JSON
const val ROTATING_NANO_IP_ROUTE_PLANNER: String = """
{
    "class": "RotatingNanoIpRoutePlanner",
    "details": {
        "ipBlock": {
            "type": "Inet6Address",
            "size": "1213123321"
        },
        "failingAddresses": [
            {
                "address": "/1.0.0.0",
                "failingTimestamp": 1573520707545,
                "failingTime": "Mon Nov 11 20:05:07 EST 2019"
            }
        ],
        "blockIndex": "0",
        "currentAddressIndex": "36792023813"
    }
}
"""

//language=JSON
const val ROTATING_IP_ROUTE_PLANNER: String = """
{
    "class": "RotatingIpRoutePlanner",
    "details": {
        "ipBlock": {
            "type": "Inet6Address",
            "size": "1213123321"
        },
        "failingAddresses": [
            {
                "address": "/1.0.0.0",
                "failingTimestamp": 1573520707545,
                "failingTime": "Mon Nov 11 20:05:07 EST 2019"
            }
        ],
        "rotateIndex": "1",
        "ipIndex": "1",
        "currentAddress": "1"
    }
}
"""

//language=JSON
const val NANO_IP_ROUTE_PLANNER: String = """
{
    "class": "NanoIpRoutePlanner",
    "details": {
        "ipBlock": {
            "type": "Inet6Address",
            "size": "1213123321"
        },
        "failingAddresses": [
            {
                "address": "/1.0.0.0",
                "failingTimestamp": 1573520707545,
                "failingTime": "Mon Nov 11 20:05:07 EST 2019"
            }
        ],
        "currentAddressIndex": 1
    }
}
"""
