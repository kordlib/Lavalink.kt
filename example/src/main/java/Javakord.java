import dev.kord.x.lavalink.interop.JavaInterop;
import dev.kord.x.lavalink.interop.JavaLavakord;
import dev.kord.x.lavalink.interop.TrackUtil;
import dev.kord.x.lavalink.interop.jda.LavakordJDABuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Javakord extends ListenerAdapter {

    private final JavaLavakord lavakord;

    public static void main(String[] args) {
        new Javakord();
    }

    public Javakord() {
        var jdaBuilder = JDABuilder.createDefault(System.getenv("TOKEN"))
                .addEventListeners(this);
        var lavakordBuilder = new LavakordJDABuilder(jdaBuilder);
        var container = lavakordBuilder.build();
        lavakord = JavaInterop.createJavaInterface(container.getLavakord());
        lavakord.addNode("ws://localhost:8080", "youshallnotpass");
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        var content = event.getMessage().getContentRaw();
        if (!content.startsWith("!")) return;
        var withoutPrefix = content.substring(1);

        var argsRaw = withoutPrefix.split("\\s+");
        var invoke = argsRaw[0];
        var args = Arrays.copyOfRange(argsRaw, 1, argsRaw.length);

        var link = lavakord.getLink(event.getGuild().getIdLong());
        var player = link.getPlayer();

        switch (invoke) {
            case "connect" -> {
                var voiceState = event.getMember().getVoiceState();
                if (voiceState == null || !voiceState.inVoiceChannel()) {
                    event.getChannel().sendMessage("Not in VC").queue();
                    return;
                }
                assert voiceState.getChannel() != null; //see check above

                link.connectAudio(voiceState.getChannel().getIdLong());
            }
            case "destroy", "ragequit", "leave" -> link.disconnectAudio();
            case "play" -> {
                var query = String.join(" ", args);
                TrackUtil.loadItem(link, query).thenAccept(track -> {
                    switch (track.getLoadType()) {
                        case TRACK_LOADED -> player.playTrack(track.getTrack());
                        case PLAYLIST_LOADED, SEARCH_RESULT -> player.playTrack(track.getTracks().get(0));
                        case NO_MATCHES -> event.getChannel().sendMessage("No tracks found!").queue();
                        case LOAD_FAILED -> event.getChannel().sendMessage("Load failed: %s".formatted(track.getException().getMessage())).queue();

                    }
                });
            }
            case "pause" -> player.pause(!player.getPaused());
            case "volume" -> {
                var volume = Integer.parseInt(args[0]);
                player.setVolume(volume);
            }
            case "seek" -> {
                var lng = Long.parseLong(args[0]) * 1000;
                var track = player.getPlayingTrack();
                if (track == null) {
                    event.getChannel().sendMessage("Not playing anything").queue();
                    return;
                }
                var newPosition = player.getPosition() + lng;
                if (newPosition < 0 || newPosition > TrackUtil.getLength(track).getSeconds() * 1000) {
                    event.getChannel().sendMessage("Position is out of bounds").queue();
                    return;
                }
                player.seekTo(newPosition);
            }
            case "eq" -> {
                var band = Integer.parseInt(args[0]);
                var gain = Float.parseFloat(args[0]);

                player.updateEqualizer()
                        .setBand(band, gain)
                        .apply(player);
            }
        }
    }
}
