@file:GenerateQueryHelper(
    serviceName = "Spotify",
    serviceWebsite = "https://spotify.com",
    generateSearchAndPlayFunction = true,
    packageName = PACKAGE_NAME,
    prefix = "spsearch",
    builderFunction = "dev.schlaubi.lavakord.internal.taggedQuery",
    parameters = [GenerateQueryHelper.Parameter("query", "", "", GenerateQueryHelper.Parameter.Type.STRING, [])],
    builderOptions = [
        GenerateQueryHelper.Parameter(
            "artist",
            "artist",
            "Searches for results from a specific arist (Only works for album, artists and tracks)",
            GenerateQueryHelper.Parameter.Type.STRING
        ),
        GenerateQueryHelper.Parameter(
            "year",
            "year",
            "Searches for results from within a specific range (eg.1955-1960) (Only works for album, artists and tracks)",
            GenerateQueryHelper.Parameter.Type.STRING
        ),
        GenerateQueryHelper.Parameter(
            "track",
            "track",
            "Searches for results containing a specific track (like Albums, Artists)",
            GenerateQueryHelper.Parameter.Type.STRING
        ),
        GenerateQueryHelper.Parameter(
            "upc",
            "upc",
            "Searches for albums with a specific upc",
            GenerateQueryHelper.Parameter.Type.STRING
        ),
        GenerateQueryHelper.Parameter(
            "isrc",
            "isrc",
            "Searches for a specific track by it's isrc",
            GenerateQueryHelper.Parameter.Type.STRING
        ),
        GenerateQueryHelper.Parameter(
            "tag",
            "tag",
            "Searches for a specific track by it's isrc",
            GenerateQueryHelper.Parameter.Type.ENUM,
            [
                GenerateQueryHelper.Parameter.EnumType(
                    "NEW",
                    "new",
                    "The `tag:new` filter will return albums released in the past two weeks"
                ),
                GenerateQueryHelper.Parameter.EnumType(
                    "HIPSTER",
                    "hipster",
                    "The `tag:hipster` can be used to return only albums with the lowest 10%% popularity."
                )
            ]
        ),
    ]
)
@file:GenerateQueryHelper(
    serviceName = "Yandex Music",
    serviceWebsite = "https://music.yandex.ru",
    generateSearchAndPlayFunction = true,
    packageName = PACKAGE_NAME,
    parameters = [GenerateQueryHelper.Parameter("query", "", "", GenerateQueryHelper.Parameter.Type.STRING, [])],
    prefix = "ymsearch"
)

@file:GenerateQueryHelper(
    serviceName = "Apple Music",
    serviceWebsite = "https://music.apple.com",
    generateSearchAndPlayFunction = true,
    packageName = PACKAGE_NAME,
    parameters = [GenerateQueryHelper.Parameter("query", "", "", GenerateQueryHelper.Parameter.Type.STRING, [])],
    prefix = "amsearch"
)

@file:GenerateQueryHelper(
    serviceName = "Deezer",
    serviceWebsite = "https://deezer.com",
    generateSearchAndPlayFunction = true,
    packageName = PACKAGE_NAME,
    parameters = [GenerateQueryHelper.Parameter("query", "", "", GenerateQueryHelper.Parameter.Type.STRING, [])],
    prefix = "dzsearch"
)

@file:GenerateQueryHelper(
    serviceName = "Deezer",
    serviceWebsite = "https://deezer.com",
    generateSearchAndPlayFunction = true,
    packageName = PACKAGE_NAME,
    parameters = [GenerateQueryHelper.Parameter("isrc", kDoc = isrcDoc)],
    prefix = "dzisrc",
    operationName = "load"
)

package dev.schlaubi.lavakord.plugins.lavasrc

import dev.schlaubi.lavakord.internal.GenerateQueryHelper

private const val PACKAGE_NAME = "dev.schlaubi.lavakord.plugins.lavasrc"
private const val isrcDoc =
    "The [ISRC](https://en.wikipedia.org/wiki/International_Standard_Recording_Code) of the song you want to play"

/**
 * Searches for items in the year range from [start] end [end].
 */
public fun SpotifySearchQueryBuilder.fromYear(start: Int, end: Int? = null) {
    year = buildString {
        append(start)
        if (end != null) {
            append('-')
            append(end)
        }
    }
}
