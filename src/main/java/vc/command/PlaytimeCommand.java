package vc.command;

import org.rusherhack.client.api.feature.command.Command;
import org.rusherhack.client.api.feature.command.arg.PlayerReference;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.command.annotations.CommandExecutor;
import vc.api.VcApi;

import java.time.Duration;
import java.util.concurrent.ForkJoinPool;

public class PlaytimeCommand extends Command {
    private final VcApi api;

    public PlaytimeCommand(final VcApi api) {
        super("vcplaytime", "Gets a player's 2b2t playtime");
        this.api = api;
        addAliases("vcpt");
    }

    @CommandExecutor
    @CommandExecutor.Argument({"player"})
    public String playtime(final PlayerReference player) {
        ForkJoinPool.commonPool().execute(() -> {
            var playtime = this.api.getPlaytime(player);
            playtime.ifPresentOrElse(
                pt -> ChatUtils.print(formatDuration(Duration.ofSeconds(pt.playtimeSeconds()))),
                () -> ChatUtils.print("Error: " + player.name() + " not found!"));
        });
        return null;
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
