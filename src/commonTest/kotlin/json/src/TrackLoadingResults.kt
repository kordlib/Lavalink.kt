package json.src

//language=JSON
private const val NEVER_GONNA_GIVE_YOU_UP: String = """
    {
      "track": "QAAAjQIAJVJpY2sgQXN0bGV5IC0gTmV2ZXIgR29ubmEgR2l2ZSBZb3UgVXAADlJpY2tBc3RsZXlWRVZPAAAAAAADPCAAC2RRdzR3OVdnWGNRAAEAK2h0dHBzOi8vd3d3LnlvdXR1YmUuY29tL3dhdGNoP3Y9ZFF3NHc5V2dYY1EAB3lvdXR1YmUAAAAAAAAAAA==",
      "info": {
        "identifier": "dQw4w9WgXcQ",
        "isSeekable": true,
        "author": "RickAstleyVEVO",
        "length": 212000,
        "isStream": false,
        "position": 0,
        "title": "Rick Astley - Never Gonna Give You Up",
        "uri": "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
      }
    }
"""

//language=JSON
const val TRACK_LOADED: String = """
    {
  "loadType": "TRACK_LOADED",
  "playlistInfo": {},
  "tracks": [
  $NEVER_GONNA_GIVE_YOU_UP
  ]
}
"""

//language=JSON
const val PLAYLIST_LOADED: String = """
    {
  "loadType": "PLAYLIST_LOADED",
  "playlistInfo": {
    "name": "Example YouTube Playlist",
    "selectedTrack": 3
  },
  "tracks": [
    $NEVER_GONNA_GIVE_YOU_UP,
    $NEVER_GONNA_GIVE_YOU_UP,
    $NEVER_GONNA_GIVE_YOU_UP
  ]
}
"""

//language=JSON
const val SEARCH_RESULT: String = """
    {
  "loadType": "SEARCH_RESULT",
  "playlistInfo": {},
  "tracks": [
    $NEVER_GONNA_GIVE_YOU_UP,
    $NEVER_GONNA_GIVE_YOU_UP,
    $NEVER_GONNA_GIVE_YOU_UP
  ]
}
"""

//language=JSON
const val NO_MATCHES: String = """
{
  "playlistInfo": {},
  "loadType": "NO_MATCHES",
  "tracks": []
}
"""

//language=JSON
const val LOAD_FAILED: String = """
    {
  "loadType": "LOAD_FAILED",
  "playlistInfo": {},
  "tracks": [],
  "exception": {
    "message": "The uploader has not made this video available in your country.",
    "severity": "COMMON"
  }
}
"""
