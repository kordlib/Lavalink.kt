package json.src

//language=JSON
const val PLAYER_UPDATE_EVENT: String = """
{
    "op": "playerUpdate",
    "guildId": "$GUILD_ID",
    "state": {
        "time": 1500467109,
        "position": 1500467109
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
  "track": "$TRACK"
}
"""

//language=JSON
const val TRACK_END_EVENT: String = """
{
  "op": "event",
  "type": "TrackEndEvent",
  "guildId": "$GUILD_ID",
  "track": "$TRACK",
  "reason": "FINISHED"
}
"""

//language=JSON
const val TRACK_EXCEPTION_EVENT: String = """
{
  "op": "event",
  "type": "TrackExceptionEvent",
  "guildId": "$GUILD_ID",
  "track": "$TRACK",
  "error": "An error occurred"
}
"""

//language=JSON
const val TRACK_STUCK_EVENT: String = """
{
  "op": "event",
  "type": "TrackStuckEvent",
  "guildId": "$GUILD_ID",
  "track": "$TRACK",
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

