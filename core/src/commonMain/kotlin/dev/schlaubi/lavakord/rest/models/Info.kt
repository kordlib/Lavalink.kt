package dev.schlaubi.lavakord.rest.models

import dev.schlaubi.lavakord.rest.models.Info.*
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Lavalink Node infomartion.
 *
 * @property version The [Version] of this Lavalink server
 * @property buildTime The millisecond unix timestamp when this Lavalink jar was built
 * @property git The [Git] information of this Lavalink server
 * @property jvm The JVM version this Lavalink server runs on
 * @property lavaplayer The Lavaplayer version being used by this server
 * @property sourceManagers The enabled source managers for this server
 * @property filters The enabled filters for this server
 * @property plugins The enabled [plugins][Plugin] for this server
 */
@Serializable
public data class Info(
    val version: Version,
    val buildTime: Int,
    val git: Git,
    val jvm: String,
    val lavaplayer: String,
    val sourceManagers: List<String>,
    val filters: List<String>,
    val plugins: List<Plugin>
) {

    /**
     * Representation of the Lavalink server version.
     *
     * @property semver The full version string of this Lavalink server
     * @property major The major version of this Lavalink server
     * @property minor The minor version of this Lavalink server
     * @property patch The patch version of this Lavalink server
     * @property preRelease The pre-release version according to semver as a . separated list of identifiers
     */
    @Serializable
    public data class Version(
        val semver: String,
        val major: Int,
        val minor: Int,
        val patch: Int,
        val preRelease: String? = null
    )

    /**
     * Representation of the Lavalink server Git information.
     *
     * @property branch The branch this Lavalink server was built
     * @property commit The commit this Lavalink server was built
     * @property commitTimeMs The millisecond unix timestamp for when the commit was created
     * @property commitTime The [Instant] in which the commit was created
     */
    @Serializable
    public data class Git(
        val branch: String,
        val commit: String,
        @SerialName("commitTime")
        val commitTimeMs: Long
    ) {
        public val commitTime: Instant by lazy { Instant.fromEpochMilliseconds(commitTimeMs) }
    }

    /**
     * Representation of a plugin.
     *
     * @property name The name of the plugin
     * @property version The version of the plugin
     */
    @Serializable
    public data class Plugin(
        val name: String,
        val version: String
    )
}
