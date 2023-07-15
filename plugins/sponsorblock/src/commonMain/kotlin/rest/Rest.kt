package dev.schlaubi.lavakord.plugins.sponsorblock.rest

import dev.schlaubi.lavakord.audio.Node
import dev.schlaubi.lavakord.audio.player.Player
import dev.schlaubi.lavakord.audio.player.guildId
import dev.schlaubi.lavakord.audio.player.node
import dev.schlaubi.lavakord.plugins.sponsorblock.model.Category
import dev.schlaubi.lavakord.rest.delete
import dev.schlaubi.lavakord.rest.get
import dev.schlaubi.lavakord.rest.put
import io.ktor.client.request.*

/**
 * Retrieves the Sponsorblock [Categories][Category] for [guild].
 */
public suspend fun Node.getSponsorblockCategories(guild: ULong): List<Category> =
    get(SponsorblockRoute.Categories(guild, sessionId))

/**
 * Retrieves the Sponsorblock [Categories][Category] for this [Player].
 */
public suspend fun Player.getSponsorblockCategories(): List<Category> =
    node.getSponsorblockCategories(guildId)

/**
 * Set's the Sponsorblock categories for [guild] to [categories].
 */
public suspend fun Node.putSponsorblockCategories(guild: ULong, categories: Iterable<Category>): Unit =
    put(SponsorblockRoute.Categories(guild, sessionId)) {
        setBody(categories.toList())
    }

/**
 * Set's the Sponsorblock categories for [guild] to [categories].
 */
public suspend fun Node.putSponsorblockCategories(guild: ULong, vararg categories: Category): Unit =
    putSponsorblockCategories(guild, categories.asIterable())

/**
 * Set's the Sponsorblock categories for this [Player] to [categories].
 */
public suspend fun Player.putSponsorblockCategories(categories: Iterable<Category>): Unit =
    node.putSponsorblockCategories(guildId, categories)

/**
 * Set's the Sponsorblock categories for this [Player] to [categories].
 */
public suspend fun Player.putSponsorblockCategories(vararg categories: Category): Unit =
    putSponsorblockCategories(categories.asIterable())

/**
 * Disables Sponsorblock for [guild].
 */
public suspend fun Node.disableSponsorblock(guild: ULong): List<Category> =
    delete(SponsorblockRoute.Categories(guild, sessionId))

/**
 * Disables Sponsorblock for this [Player].
 */
public suspend fun Player.disableSponsorblock(): List<Category> =
    node.disableSponsorblock(guildId)
