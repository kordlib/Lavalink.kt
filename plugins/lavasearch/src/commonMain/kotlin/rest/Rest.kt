package dev.schlaubi.lavakord.plugins.lavasearch.rest

import com.github.topi314.lavasearch.protocol.SearchResult
import dev.schlaubi.lavakord.audio.Link
import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.plugins.lavasearch.model.SearchType
import dev.schlaubi.lavakord.rest.get

/**
 * Searches for [query].
 *
 * @param types the allowed [SearchTypes][SearchType]
 * @see SearchResult
 */
public suspend fun Node.search(query: String, vararg types: SearchType): SearchResult =
    search(query, types.asIterable())

/**
 * Searches for [query].
 *
 * @param types the allowed [SearchTypes][SearchType]
 * @see SearchResult
 */
public suspend fun Node.search(query: String, types: Iterable<SearchType>): SearchResult =
    get(LavaSearchRoute(query, types.toList()))

/**
 * Searches for [query].
 *
 * @param types the allowed [SearchTypes][SearchType]
 * @see SearchResult
 */
public suspend fun Link.search(query: String, vararg types: SearchType): SearchResult = node.search(query, *types)

/**
 * Searches for [query].
 *
 * @param types the allowed [SearchTypes][SearchType]
 * @see SearchResult
 */
public suspend fun Link.search(query: String, types: Iterable<SearchType>): SearchResult = node.search(query, types)
