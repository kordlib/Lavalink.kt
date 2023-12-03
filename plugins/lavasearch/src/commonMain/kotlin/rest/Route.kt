package dev.schlaubi.lavakord.plugins.lavasearch.rest

import dev.schlaubi.lavakord.plugins.lavasearch.model.SearchType
import dev.schlaubi.lavakord.rest.routes.V4Api
import io.ktor.resources.*

@Resource("loadsearch")
internal data class LavaSearchRoute(val query: String, val types: List<SearchType>, val player: V4Api = V4Api())
