import dev.kord.x.lavalink.interop.JavaInterop
import dev.kord.x.lavalink.interop.JavaLavakord
import dev.kord.x.lavalink.interop.TrackUtil
import dev.kord.x.lavalink.interop.jda.LavakordJDABuilder
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.AnnotatedEventManager
import net.dv8tion.jda.api.hooks.SubscribeEvent
import org.jetbrains.annotations.NotNull

static void main(String[] args) {
    var jdaBuilder = JDABuilder.createDefault(System.getenv("TOKEN"))
            .setEventManager(new AnnotatedEventManager())
    var lavakordBuilder = new LavakordJDABuilder(jdaBuilder)
    var container = lavakordBuilder.build()
    var lavakord = JavaInterop.createJavaInterface(container.getLavakord())
    container.getJda().addEventListener(new CommandHandler(lavakord))
    lavakord.addNode("ws://localhost:8080", "youshallnotpass")
}

class CommandHandler {
    private final JavaLavakord javakord

    CommandHandler(JavaLavakord javakord) {
        this.javakord = javakord
    }

    @SuppressWarnings('unused')
    @SubscribeEvent
    void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        var content = event.getMessage().getContentRaw()
        if (!content.startsWith("!")) return
        var withoutPrefix = content.substring(1)

        var argsRaw = withoutPrefix.split("\\s+")
        var invoke = argsRaw[0]
        var args = Arrays.copyOfRange(argsRaw, 1, argsRaw.length)

        var link = javakord.getLink(event.getGuild().getIdLong())
        var player = link.getPlayer()

        switch (invoke) {
            case "connect":
                var voiceState = event.getMember().getVoiceState()
                if (voiceState == null || !voiceState.inVoiceChannel()) {
                    event.getChannel().sendMessage("Not in VC").queue()
                    break
                }
                assert voiceState.getChannel() != null //see check above
                link.connectAudio(voiceState.getChannel().getIdLong())
                break
            case "destroy":
            case "ragequit":
            case "leave":
                link.disconnectAudio()
                break
            case "play":
                var query = String.join(" ", args)
                TrackUtil.loadItem(link, query).thenAccept({ track ->
                    switch (track.getLoadType()) {
                        case TRACK_LOADED:
                            player.playTrack(track.getTrack())
                            break
                        case PLAYLIST_LOADED:
                        case SEARCH_RESULT: player.playTrack(track.getTracks().get(0))
                            break
                        case NO_MATCHES:
                            event.getChannel().sendMessage("No tracks found!").queue()
                            break
                        case LOAD_FAILED:
                            event.getChannel().sendMessage("Load failed: %s".formatted(track.getException().getMessage())).queue()
                            break

                    }
                })
                break
            case "pause":
                player.pause(!player.getPaused())
                break
            case "volume":
                var volume = Integer.parseInt(args[0])
                player.setVolume(volume)
                break
            case "seek":
                var lng = Long.parseLong(args[0]) * 1000
                var track = player.getPlayingTrack()
                if (track == null) {
                    event.getChannel().sendMessage("Not playing anything").queue()
                    break
                }
                var newPosition = player.getPosition() + lng
                if (newPosition < 0 || newPosition > TrackUtil.getLength(track).getSeconds() * 1000) {
                    event.getChannel().sendMessage("Position is out of bounds").queue()
                    break
                }
                player.seekTo(newPosition)
                break
            case "eq":
                var band = Integer.parseInt(args[0])
                var gain = Float.parseFloat(args[0])
                player.updateEqualizer()
                        .setBand(band, gain)
                        .apply(player)
                break
        }
    }
}
