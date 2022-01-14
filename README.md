# Lavalink.kt

Lavakord is a coroutine based client for [LavaLink](https://github.com/Fredikaram/Lavalink) mainly designed to work
with [Kord](https://github.com/kordlib/kord)

Support discord: https://discord.gg/ZbmrRVpDwR

**Both Kord and Lavalink.kt is still in an experimental stage, as such we can't guarantee API stability between
releases. While we'd love for you to try out our library, we don't recommend you use this in production just yet.**

Lavalink download: [https://github.com/Frederikam/Lavalink](https://github.com/Frederikam/Lavalink#server-configuration)

# Download

You can get Lavakord from
here: https://schlaubi.jfrog.io/ui/repos/tree/General/lavakord%2Fdev%2Fschlaubi%2Flavakord

# Usage

You can create a `Lavalink` object like this

```kotlin
    val lavalink = kord.lavakord()

// or    

val lavalink = kord.lavakord {
    link {
        autoReconnect = false
        retry = linear(2.seconds, 60.seconds, 10)
    }
}
```

You can obtain and use a `Link` like this

```kotlin
    val link = guild.getLink(lavalink)

link.connect(channel)

// use lavalink stuff like player

link.disconnect()
```

Playing: https://github.com/DRSchlaubi/Lavakord/blob/master/example/src/main/kotlin/me/schlaubi/lavakord/example/Lavakord.kt#L99-124

# Track loading

Lavakord provides a wrapper for the
Lavalink [Track loading API](https://github.com/Frederikam/Lavalink/blob/master/IMPLEMENTATION.md#track-loading-api)

You can load a Track by using `Link.loadItem(query: String)`

# Events

Since 0.3 Lavakord provides a [Flow based](https://kotlinlang.org/docs/reference/coroutines/flow.html) way to listen for
events.

```kotlin
val link: KordLink // = .../

val player = link.player

player.on<TrackStartEvent> {
    channel.createMessage(track.info.asString())
}
```

# Documentation

For more info please use the [example](https://github.com/DRSchlaubi/Lavakord/blob/master/example)
or [Dokka docs](https://l.mik.wtf/)

# Multiplatform

Since Lavakord 1.0 we use only Multiplatform Kotlin libraries but Ktor doesn't support Websockets when using Kotlin
native yet see [kordlib/kord#69](https://github.com/kordlib/kord/issues/69)
and [ktorio/ktor#1215](https://github.com/ktorio/ktor/issues/1215) for reference. Kord doesn't support Multiplatform because of the same issue as well

Since 2.0 JS is officially supported

# Other Discord API wrappers

Since 1.0 it should be possible to implement your own version of lavakord by implementing your own versions of the
LavaKord and Link classes you can see a reference
implementation [in the kord package](https://github.com/DRSchlaubi/Lavakord/blob/master/src/main/kotlin/me/schlaubi/lavakord/kord)

# Using with Kord
Add the `kord` artifact

# Using with JDA

Apart from Kord there also is a JDA implementation. You can use it like the following. (Requires `jda` artifact)

```kotlin
var (lavakord, jda) = JDABuilder.createDefault("token").buildWithLavakord()
```

```java
class Javakord {
    var container = new LavakordJDABuilder(JDABuilder.createDefault("token")).build();
    var jda = container.getJda();
    var lavakord = container.getLavakord();
    JavaLavakord javaLavakord = JavaInterop.createJavaInterface(lavakord);
}
```

The snippets work similarly for `DefaultShardManagerBuilder` as well.

# Using with Java

Lavakord provides a compatibility layer for coroutines based
on [Java 8's CompletableFuture API](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html)
. To access that interface please use the `JavaInterop` class. For all rest related features refer to the `TrackUtil`
and `RoutePlannerUtil` classes. In order to use these methods please add the `java` or `jda-java` artifact

Full example can be found [here](https://github.com/DRSchlaubi/Lavakord/blob/feature/mpp/example/src/main/java/Javakord.java) 

```java
class Javakord {
    Lavakord lavakord;// = <lavakord build mechanism>
    JavaLavakord javaLavakord = JavaInterop.createJavaInterface(lavakord);
}
```
