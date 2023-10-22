package vc.command;

import org.rusherhack.client.api.feature.command.Command;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.command.annotations.CommandExecutor;
import vc.api.VcApi;

import java.time.Duration;
import java.util.concurrent.ForkJoinPool;

public class PlaytimeCommand extends Command {
    private final VcApi api;

    public PlaytimeCommand(final VcApi api) {
        super("playtimevc", "Gets a player's 2b2t playtime");
        this.api = api;
    }

    @CommandExecutor
    @CommandExecutor.Argument({"playerName"})
    public void playtime(final String playerName) {
        ForkJoinPool.commonPool().execute(() -> {
            var playtime = this.api.getPlaytime(playerName);
            if (playtime.isEmpty()) {
                ChatUtils.print("Error: " + playerName + " not found!");
                return;
            }
            ChatUtils.print(formatDuration(Duration.ofSeconds(playtime.get().playtimeSeconds())));
        });
    }

    public static String formatDuration(Duration duration) {
        final StringBuilder sb = new StringBuilder();
        if (duration.toDaysPart() > 0) sb.append(duration.toDaysPart()).append("d ");
        if (duration.toHoursPart() > 0) sb.append(duration.toHoursPart()).append("h ");
        if (duration.toMinutesPart() > 0) sb.append(duration.toMinutesPart()).append("m ");
        sb.append(duration.toSecondsPart()).append("s");
        return sb.toString();
    }
}
