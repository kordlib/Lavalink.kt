@file:Generate(
    Generate.EntityType.STRING_KORD_ENUM,
    "SearchType",
    docUrl = "https://github.com/topi314/LavaSearch?tab=readme-ov-file#api",
    entries = [
        Generate.Entry(
            "Track",
            stringValue = "track",
            kDoc = "Tracks."
        ),
        Generate.Entry(
            "Album",
            stringValue = "album",
            kDoc = "Album."
        ),
        Generate.Entry(
            "Artist",
            stringValue = "artist",
            kDoc = "Artists."
        ),
        Generate.Entry(
            "Playlist",
            stringValue = "playlist",
            kDoc = "Playlists."
        ),
        Generate.Entry(
            "Text",
            stringValue = "text",
            kDoc = "Search suggestions."
        )
    ]
)

package dev.schlaubi.lavakord.plugins.lavasearch.model

import dev.kord.ksp.Generate
