package dev.schlaubi.lavakord;

import dev.arbjerg.lavalink.protocol.v4.LoadResult;
import dev.schlaubi.lavakord.audio.TrackEndEvent;
import dev.schlaubi.lavakord.interop.JavaInterop;
import dev.schlaubi.lavakord.interop.JavaLavakord;
import dev.schlaubi.lavakord.interop.TrackUtil;
import dev.schlaubi.lavakord.interop.jda.LavakordJDABuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

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
        var jda = container.getJda();
        jda.updateCommands()
                .addCommands(
                        Commands.slash("connect", "Joins the current channel"),
                        Commands.slash("play", "Plays a new song")
                                .addOption(OptionType.STRING, "query", "The query you want to play"),
                        Commands.slash("destroy", "Let the bot leave the channel"),
                        Commands.slash("pause", "Pauses or unpauses playpack")
                )
                .queue();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        var invoke = event.getName();
        var link = lavakord.getLink(event.getGuild().getIdLong());
        var player = link.getPlayer();

        switch (invoke) {
            case "connect" -> {
                var voiceState = event.getMember().getVoiceState();
                if (voiceState == null || !voiceState.inAudioChannel()) {
                    event.getChannel().sendMessage("Not in VC").queue();
                    return;
                }
                assert voiceState.getChannel() != null; //see check above

                link.connectAudio(voiceState.getChannel().getIdLong());
            }
            case "destroy" -> link.disconnectAudio();
            case "play" -> {
                var query = event.getOption("query").getAsString();
                TrackUtil.loadItem(link, query).thenAccept(track -> {
                    switch (track) {
                        case LoadResult.TrackLoaded trackLoaded -> player.playTrack(trackLoaded.getData());
                        case LoadResult.PlaylistLoaded playlistLoaded -> player.playTrack(playlistLoaded.getData().getTracks().get(0));
                        case LoadResult.SearchResult searchResult -> player.playTrack(searchResult.getData().getTracks().get(0));
                        case LoadResult.NoMatches ignored -> event.getChannel().sendMessage("No tracks found!").queue();
                        case LoadResult.LoadFailed loadFailed ->
                                event.getChannel().sendMessage("Load failed: %s".formatted(loadFailed.getData().getMessage())).queue();
                        // Even though this is sealed it doesn't work
                        default -> throw new IllegalStateException("Unexpected value: " + track);
                    }
                });
            }
            case "pause" -> player.pause(!player.getPaused());
            case "seek" -> {
                var lng = event.getOption("position").getAsLong() * 1000;
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
                var band = (int) event.getOption("band").getAsLong();
                var gain = (float) event.getOption("gain").getAsDouble();

                player.updateEqualizer()
                        .setBand(band, gain)
                        .apply(player);
            }
        }
    }
}
