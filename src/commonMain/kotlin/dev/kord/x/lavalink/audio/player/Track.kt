package dev.kord.x.lavalink.audio.player

import io.ktor.util.*
import io.ktor.utils.io.*
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/**
 * Representation of a Lavalink audio track.
 *
 * @property version version of [track] format (i guess no one actually knows what it is)
 * @property track the base64 encoded string representing a Lavalink readable track
 * @property title the title of the track
 * @property author the author of the track
 * @property length the length of the track as a [Duration]
 * @property identifier the identifier for the track (e.g. youtube video id)
 * @property isStream whether this track is a stream or not
 * @property isSeekable whether this track is seekable or not (opposite of [isStream])
 * @property uri the full url of the track (if it's not a local track)
 * @property source the Lavaplayer source of this track (e.g. youtube)
 * @property position the current position of the track (normally 0 or [length]) (See [Player.position]
 */
@OptIn(ExperimentalTime::class)
public data class Track constructor(
    public val version: Byte,
    public val track: String,
    public val title: String,
    public val author: String,
    public val length: Duration,
    public val identifier: String,
    public val isStream: Boolean,
    public val isSeekable: Boolean,
    public val uri: String?,
    public val source: String,
    public val position: Duration
) {
    public companion object {
        /**
         * Converts a lavalink encoded track to a [Track].
         *
         * @see Track.track
         */
        @OptIn(InternalAPI::class)
        @Suppress("ReplaceNotNullAssertionWithElvisReturn")
        public suspend fun fromLavalink(encoded: String): Track {
            val bytes = encoded.decodeBase64Bytes()
            val reader = ByteReadChannel(bytes)

            val flags = (reader.readInt() and 0xC0000000.toInt()) shr 30
            val version = if ((flags and 1) != 0) reader.readByte() else 1

            val title = reader.readUTF()
            val author = reader.readUTF()
            val length = reader.readLong()
            val identifier = reader.readUTF()
            val isStream = reader.readBoolean()
            val isSeekable = !isStream
            val uri = if (reader.readBoolean()) reader.readUTF() else null
            val source = reader.readUTF()
            val position = reader.readLong()

            return Track(
                version,
                encoded,
                title,
                author,
                length.toDuration(DurationUnit.MILLISECONDS),
                identifier,
                isStream,
                isSeekable,
                uri,
                source,
                position.toDuration(DurationUnit.MILLISECONDS)
            )
        }
    }
}

private suspend fun ByteReadChannel.readUTF(): String {
    val length = readShort().toInt()
    return readPacket(length).readText()
}
