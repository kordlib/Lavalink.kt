package json.src

const val GUILD_ID: Long = -1L
const val SESSION_ID: Long = -2L

// NEVVVA GONA GIVE YOU UP NEVVA GONNA LET YOU DOWN
//language=text
const val TRACK = "QAAAjQIAJVJpY2sgQXN0bGV5IC0gTmV2ZXIgR29ubmEgR2l2ZSBZb3UgVXAADlJpY2tBc3RsZXlWRVZPAAAAAAADPCAAC2RRdzR3OVdnWGNRAAEAK2h0dHBzOi8vd3d3LnlvdXR1YmUuY29tL3dhdGNoP3Y9ZFF3NHc5V2dYY1EAB3lvdXR1YmUAAAAAAAAAAA"

//language=JSON
const val PLAY_COMMAND: String = """
{
    "op": "play",
    "guildId": "$GUILD_ID",
    "track": "$TRACK",
    "startTime": "60000",
    "endTime": "120000",
    "volume": "100",
    "noReplace": false,
    "pause": false
}
"""

//language=JSON
const val STOP_COMMAND: String = """
{
    "op": "stop",
    "guildId": "$GUILD_ID"
}
"""

//language=JSON
const val PAUSE_COMMAND: String = """
{
    "op": "pause",
    "pause": true,
    "guildId": "$GUILD_ID"
}

"""//language=JSON
const val SEEK_COMMAND: String = """
{
    "op": "seek",
    "guildId": "$GUILD_ID",
    "position": 60000
}
"""

//language=JSON
const val VOLUME_COMMAND: String = """
{
    "op": "volume",
    "guildId": "$GUILD_ID",
    "volume": 125
}
"""

//language=JSON
const val EQUALIZER_COMMAND: String = """
{
    "op": "equalizer",
    "guildId": "$GUILD_ID",
    "bands": [
        {
            "band": 0,
            "gain": 0.2
        }
    ]
}
"""

//language=JSON
const val DESTROY_COMMAND: String = """
{
    "op": "destroy",
    "guildId": "$GUILD_ID"
}
"""