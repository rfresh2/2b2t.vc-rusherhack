package vc.command;

import org.rusherhack.client.api.feature.command.Command;
import org.rusherhack.client.api.feature.command.arg.PlayerReference;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.command.annotations.CommandExecutor;
import vc.api.VcApi;

import java.time.Duration;
import java.util.concurrent.ForkJoinPool;

import static vc.util.FormatUtil.formatDuration;

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
            var out = player.name() + " Playtime\n" +
                playtime.map(
                    pt -> formatDuration(Duration.ofSeconds(pt.playtimeSeconds())))
                    .orElse("Error: " + player.name() + " not found!");
            ChatUtils.print(out);
        });
        return null;
    }
}
