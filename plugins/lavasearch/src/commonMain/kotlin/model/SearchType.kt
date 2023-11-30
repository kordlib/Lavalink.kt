@file:GenerateKordEnum(
    "SearchType",
    GenerateKordEnum.ValueType.STRING,
    docUrl = "https://github.com/topi314/LavaSearch?tab=readme-ov-file#api",
    entries = [
        GenerateKordEnum.Entry(
            "Track",
            stringValue = "track",
            kDoc = "Tracks."
        ),
        GenerateKordEnum.Entry(
            "Album",
            stringValue = "album",
            kDoc = "Album."
        ),
        GenerateKordEnum.Entry(
            "Artist",
            stringValue = "artist",
            kDoc = "Artists."
        ),
        GenerateKordEnum.Entry(
            "Playlist",
            stringValue = "playlist",
            kDoc = "Playlists."
        ),
        GenerateKordEnum.Entry(
            "Text",
            stringValue = "text",
            kDoc = "Search suggestions."
        )
    ]
)

package dev.schlaubi.lavakord.plugins.lavasearch.model

import dev.kord.ksp.GenerateKordEnum
