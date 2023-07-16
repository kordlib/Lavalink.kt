@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:GenerateQueryHelper(
    serviceName = "Spotify",
    serviceWebsite = "https://spotify.com",
    prefix = "sprec",
    generateSearchAndPlayFunction = false,
    packageName = PACKAGE_NAME,
    builderFunction = "dev.schlaubi.lavakord.internal.query",
    operationName = "recommend",
    parameters = [
        GenerateQueryHelper.Parameter(
            name = "seedArtists",
            queryName = "seed_artists",
            kDoc = "A comma separated list of [Spotify·IDs](https://developer.spotify.com/documentation/web-api/concepts/spotify-uris-ids) for seed artists"
        ),
        GenerateQueryHelper.Parameter(
            name = "seedGenres",
            queryName = "seed_genres",
            kDoc = "A comma separated list of any genres in the set of [available·genre·seeds](https://developer.spotify.com/documentation/web-api/reference/get-recommendations#available-genre-seeds)"
        ),
        GenerateQueryHelper.Parameter(
            name = "seedTracks",
            queryName = "seed_tracks",
            kDoc = "A comma separated list of [Spotify·IDs](https://developer.spotify.com/documentation/web-api/concepts/spotify-uris-ids) for seed tracks"
        ),
    ],
    builderOptions = [
        GenerateQueryHelper.Parameter(
            name = "limit",
            queryName = "limit",
            kDoc = "The target size of the list of recommended tracks. For seeds with unusually small pools or when highly restrictive filtering is applied, it may be impossible to generate the requested number of recommended tracks. Debugging information for such cases is available in the response. Default: 20. Minimum: 1. Maximum: 100.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "minAcousticness",
            queryName = "min_acousticness",
            kDoc = "For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `min_tempo=140` would restrict results to only those tracks with a tempo of greater than 140 beats per minute.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "maxAcousticness",
            queryName = "max_acousticness",
            kDoc = "For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `max_instrumentalness=0.35` would filter out most tracks that are likely to be instrumental.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "targetAcousticness",
            queryName = "target_acousticness",
            kDoc = "For each of the tunable track attributes (below) a target value may be provided. Tracks with the attribute values nearest to the target values will be preferred. For example, you might request `target_energy=0.6` and `target_danceability=0.8`. All target values will be weighed equally in ranking results.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "minDanceability",
            queryName = "min_danceability",
            kDoc = "For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `min_tempo=140` would restrict results to only those tracks with a tempo of greater than 140 beats per minute.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "maxDanceability",
            queryName = "max_danceability",
            kDoc = "For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `max_instrumentalness=0.35` would filter out most tracks that are likely to be instrumental.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "targetDanceability",
            queryName = "target_danceability",
            kDoc = "For each of the tunable track attributes (below) a target value may be provided. Tracks with the attribute values nearest to the target values will be preferred. For example, you might request `target_energy=0.6` and `target_danceability=0.8`. All target values will be weighed equally in ranking results.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "minDurationMs",
            queryName = "min_duration_ms",
            kDoc = "For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `min_tempo=140` would restrict results to only those tracks with a tempo of greater than 140 beats per minute.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "maxDurationMs",
            queryName = "max_duration_ms",
            kDoc = "For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `max_instrumentalness=0.35` would filter out most tracks that are likely to be instrumental.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "targetDurationMs",
            queryName = "target_duration_ms",
            kDoc = "Target duration of the track (ms)",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "minEnergy",
            queryName = "min_energy",
            kDoc = "For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `min_tempo=140` would restrict results to only those tracks with a tempo of greater than 140 beats per minute.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "maxEnergy",
            queryName = "max_energy",
            kDoc = "For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `max_instrumentalness=0.35` would filter out most tracks that are likely to be instrumental.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "targetEnergy",
            queryName = "target_energy",
            kDoc = "For each of the tunable track attributes (below) a target value may be provided. Tracks with the attribute values nearest to the target values will be preferred. For example, you might request `target_energy=0.6` and `target_danceability=0.8`. All target values will be weighed equally in ranking results.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "minInstrumentalness",
            queryName = "min_instrumentalness",
            kDoc = "For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `min_tempo=140` would restrict results to only those tracks with a tempo of greater than 140 beats per minute.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "maxInstrumentalness",
            queryName = "max_instrumentalness",
            kDoc = "For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `max_instrumentalness=0.35` would filter out most tracks that are likely to be instrumental.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "targetInstrumentalness",
            queryName = "target_instrumentalness",
            kDoc = "For each of the tunable track attributes (below) a target value may be provided. Tracks with the attribute values nearest to the target values will be preferred. For example, you might request `target_energy=0.6` and `target_danceability=0.8`. All target values will be weighed equally in ranking results.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "minKey",
            queryName = "min_key",
            kDoc = "For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `min_tempo=140` would restrict results to only those tracks with a tempo of greater than 140 beats per minute.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "maxKey",
            queryName = "max_key",
            kDoc = "For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `max_instrumentalness=0.35` would filter out most tracks that are likely to be instrumental.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "targetKey",
            queryName = "target_key",
            kDoc = "For each of the tunable track attributes (below) a target value may be provided. Tracks with the attribute values nearest to the target values will be preferred. For example, you might request `target_energy=0.6` and `target_danceability=0.8`. All target values will be weighed equally in ranking results.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "minLiveness",
            queryName = "min_liveness",
            kDoc = "For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `min_tempo=140` would restrict results to only those tracks with a tempo of greater than 140 beats per minute.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "maxLiveness",
            queryName = "max_liveness",
            kDoc = "For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `max_instrumentalness=0.35` would filter out most tracks that are likely to be instrumental.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "targetLiveness",
            queryName = "target_liveness",
            kDoc = "For each of the tunable track attributes (below) a target value may be provided. Tracks with the attribute values nearest to the target values will be preferred. For example, you might request `target_energy=0.6` and `target_danceability=0.8`. All target values will be weighed equally in ranking results.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "minLoudness",
            queryName = "min_loudness",
            kDoc = "For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `min_tempo=140` would restrict results to only those tracks with a tempo of greater than 140 beats per minute.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "maxLoudness",
            queryName = "max_loudness",
            kDoc = "For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `max_instrumentalness=0.35` would filter out most tracks that are likely to be instrumental.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "targetLoudness",
            queryName = "target_loudness",
            kDoc = "For each of the tunable track attributes (below) a target value may be provided. Tracks with the attribute values nearest to the target values will be preferred. For example, you might request `target_energy=0.6` and `target_danceability=0.8`. All target values will be weighed equally in ranking results.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "minMode",
            queryName = "min_mode",
            kDoc = "For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `min_tempo=140` would restrict results to only those tracks with a tempo of greater than 140 beats per minute.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "maxMode",
            queryName = "max_mode",
            kDoc = "For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `max_instrumentalness=0.35` would filter out most tracks that are likely to be instrumental.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "targetMode",
            queryName = "target_mode",
            kDoc = "For each of the tunable track attributes (below) a target value may be provided. Tracks with the attribute values nearest to the target values will be preferred. For example, you might request `target_energy=0.6` and `target_danceability=0.8`. All target values will be weighed equally in ranking results.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "minPopularity",
            queryName = "min_popularity",
            kDoc = "For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `min_tempo=140` would restrict results to only those tracks with a tempo of greater than 140 beats per minute.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "maxPopularity",
            queryName = "max_popularity",
            kDoc = "For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `max_instrumentalness=0.35` would filter out most tracks that are likely to be instrumental.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "targetPopularity",
            queryName = "target_popularity",
            kDoc = "For each of the tunable track attributes (below) a target value may be provided. Tracks with the attribute values nearest to the target values will be preferred. For example, you might request `target_energy=0.6` and `target_danceability=0.8`. All target values will be weighed equally in ranking results.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "minSpeechiness",
            queryName = "min_speechiness",
            kDoc = "For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `min_tempo=140` would restrict results to only those tracks with a tempo of greater than 140 beats per minute.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "maxSpeechiness",
            queryName = "max_speechiness",
            kDoc = "For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `max_instrumentalness=0.35` would filter out most tracks that are likely to be instrumental.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "targetSpeechiness",
            queryName = "target_speechiness",
            kDoc = "For each of the tunable track attributes (below) a target value may be provided. Tracks with the attribute values nearest to the target values will be preferred. For example, you might request `target_energy=0.6` and `target_danceability=0.8`. All target values will be weighed equally in ranking results.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "minTempo",
            queryName = "min_tempo",
            kDoc = "For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `min_tempo=140` would restrict results to only those tracks with a tempo of greater than 140 beats per minute.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "maxTempo",
            queryName = "max_tempo",
            kDoc = "For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `max_instrumentalness=0.35` would filter out most tracks that are likely to be instrumental.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "targetTempo",
            queryName = "target_tempo",
            kDoc = "Target tempo (BPM)",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "minTimeSignature",
            queryName = "min_time_signature",
            kDoc = "For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `min_tempo=140` would restrict results to only those tracks with a tempo of greater than 140 beats per minute.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "maxTimeSignature",
            queryName = "max_time_signature",
            kDoc = "For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `max_instrumentalness=0.35` would filter out most tracks that are likely to be instrumental.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "targetTimeSignature",
            queryName = "target_time_signature",
            kDoc = "For each of the tunable track attributes (below) a target value may be provided. Tracks with the attribute values nearest to the target values will be preferred. For example, you might request `target_energy=0.6` and `target_danceability=0.8`. All target values will be weighed equally in ranking results.",
            type = GenerateQueryHelper.Parameter.Type.INT
        ),
        GenerateQueryHelper.Parameter(
            name = "minValence",
            queryName = "min_valence",
            kDoc = "For each tunable track attribute, a hard floor on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `min_tempo=140` would restrict results to only those tracks with a tempo of greater than 140 beats per minute.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "maxValence",
            queryName = "max_valence",
            kDoc = "For each tunable track attribute, a hard ceiling on the selected track attribute’s value can be provided. See tunable track attributes below for the list of available options. For example, `max_instrumentalness=0.35` would filter out most tracks that are likely to be instrumental.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
        GenerateQueryHelper.Parameter(
            name = "targetValence",
            queryName = "target_valence",
            kDoc = "For each of the tunable track attributes (below) a target value may be provided. Tracks with the attribute values nearest to the target values will be preferred. For example, you might request `target_energy=0.6` and `target_danceability=0.8`. All target values will be weighed equally in ranking results.",
            type = GenerateQueryHelper.Parameter.Type.DOUBLE
        ),
    ]
)

package dev.schlaubi.lavakord.plugins.lavasrc

import dev.schlaubi.lavakord.internal.GenerateQueryHelper

