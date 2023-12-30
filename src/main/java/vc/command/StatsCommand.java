package vc.command;

import org.rusherhack.client.api.feature.command.Command;
import org.rusherhack.client.api.feature.command.arg.PlayerReference;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.command.annotations.CommandExecutor;
import vc.api.VcApi;

import java.time.Duration;
import java.util.concurrent.ForkJoinPool;

import static vc.util.FormatUtil.formatDuration;
import static vc.util.FormatUtil.getSeenString;

public class StatsCommand extends Command {
    private final VcApi api;
    public StatsCommand(final VcApi api) {
        super("stats", "Gets the 2b2t stats of a player");
        this.api = api;
    }

    @CommandExecutor
    @CommandExecutor.Argument({"player"})
    private String statsPlayerName(final PlayerReference player) {
        ForkJoinPool.commonPool().execute(() -> {
            var statsResponse = api.getStats(player);
            var out = statsResponse.map(s ->
                player.name() + " Stats" +
                "\nJoins: " + s.joinCount() +
                "\nLeaves: " + s.leaveCount() +
                "\nFirst Seen: " + getSeenString(s.firstSeen()) +
                "\nLast Seen: " + getSeenString(s.lastSeen()) +
                "\nPlaytime: " + formatDuration(Duration.ofSeconds(s.playtimeSeconds())) +
                "\nPlaytime (Last 30 Days): " + formatDuration(Duration.ofSeconds(s.playtimeSecondsMonth())) +
                "\nDeaths: " + s.deathCount() +
                "\nKills: " + s.killCount() +
                "\nChats: " + s.chatsCount())
                .orElse("Error: " + player.name() + " not found!");
            ChatUtils.print(out);
        });
        return null;
    }
}
