# Lavakord
Extension of the [official LavaLink-Client](https://github.com/FredBoat/Lavalink-Client) to work with [Kord](https://github.com/kordlib/kord)

**Both Kord and Lavakord is still in an experimental stage, as such we can't guarantee API stability between releases. While we'd love for you to try out our library, we don't recommend you use this in production just yet.**

Lavalink download: [https://github.com/Frederikam/Lavalink](https://github.com/Frederikam/Lavalink#server-configuration)

# Download
You can get Lavakord from here: [https://package-search.jetbrains.com/package?id=me.schlaubi%3Alavakord](https://package-search.jetbrains.com/package?id=me.schlaubi%3Alavakord) (You need `jcenter()` and jitpack though)

# Usage
You can create a `Lavalink` object like this
```kotlin
    val lavalink = kord.lavalink()

    // or    

    val lavalink = kord.lavalink {
            autoReconnect = false
    }
```

You can obtain and use a `Link` like this
```kotlin
    val link = guild.getLink(lavalink)

    link.connect(channel)

    // use lavalink stuff like player

    link.disconnect()
```

# Documentation
For more info please use the [example](https://github.com/DRSchlaubi/Lavakord/blob/master/example) or [Dokka docs](https://l.mik.wtf/lavakord/)