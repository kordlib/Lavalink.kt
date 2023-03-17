package json.src

const val GUILD_ID = 1UL

// NEVVVA GONA GIVE YOU UP NEVVA GONNA LET YOU DOWN
//language=text
const val TRACK = "QAAAjQIAJVJpY2sgQXN0bGV5IC0gTmV2ZXIgR29ubmEgR2l2ZSBZb3UgVXAADlJpY2tBc3RsZXlWRVZPAAAAAAADPCAAC2RRdzR3OVdnWGNRAAEAK2h0dHBzOi8vd3d3LnlvdXR1YmUuY29tL3dhdGNoP3Y9ZFF3NHc5V2dYY1EAB3lvdXR1YmUAAAAAAAAAAA"


//language=JSON
const val PLAYER_UPDATE_EVENT: String = """
{
    "op": "playerUpdate",
    "guildId": "$GUILD_ID",
    "state": {
        "time": 1500467109,
        "position": 1500467109,
        "connected": true,
        "ping": 1337
    }
}
"""

//language=JSON
const val FULL_STATS_EVENT: String = """
{
    "op": "stats",
    "players": 1,
    "playingPlayers": 1,
    "uptime": 10,
    "memory": {
      "free": "100",
      "used": "100",
      "allocated": "100",
      "reservable": "100"
    },
    "cpu": {
      "cores": 10,
      "systemLoad": 0.50,
      "lavalinkLoad": 0.10
    },
    "frameStats": {
      "sent": 10,
      "nulled": 10,
      "deficit": 10
    }
}
"""

//language=JSON
const val STATS_EVENT: String = """
{
    "op": "stats",
    "uptime": 10,
    "players": 1,
    "playingPlayers": 1,
    "memory": {
      "free": "100",
      "used": "100",
      "allocated": "100",
      "reservable": "100"
    },
    "cpu": {
      "cores": 10,
      "systemLoad": 0.50,
      "lavalinkLoad": 0.10
    }
}
"""

//language=JSON
const val TRACK_START_EVENT: String = """
{
  "op": "event",
  "type": "TrackStartEvent",
  "guildId": "$GUILD_ID",
  "encodedTrack": "$TRACK"
}
"""

//language=JSON
const val TRACK_END_EVENT: String = """
{
  "op": "event",
  "type": "TrackEndEvent",
  "guildId": "$GUILD_ID",
  "encodedTrack": "$TRACK",
  "reason": "FINISHED"
}
"""

//language=JSON
const val TRACK_EXCEPTION_EVENT: String = """
{
  "op": "event",
  "type": "TrackExceptionEvent",
  "guildId": "$GUILD_ID",
  "encodedTrack": "$TRACK",
  "exception": {
    "message": "...",
    "severity": "COMMON",
    "cause": "..."
  }
}"""

//language=JSON
const val TRACK_STUCK_EVENT: String = """
{
  "op": "event",
  "type": "TrackStuckEvent",
  "guildId": "$GUILD_ID",
  "encodedTrack": "$TRACK",
  "thresholdMs": 500
}
"""

//language=JSON
const val WEBSOCKET_CLOSED_EVENT: String = """
{
    "op": "event",
    "type": "WebSocketClosedEvent",
    "guildId": "$GUILD_ID",
    "code": 4006,
    "reason": "Your session is no longer valid.",
    "byRemote": true
}
"""

