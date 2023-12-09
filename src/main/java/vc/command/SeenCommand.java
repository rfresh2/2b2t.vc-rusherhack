package vc.command;

import org.jetbrains.annotations.Nullable;
import org.rusherhack.client.api.feature.command.Command;
import org.rusherhack.client.api.feature.command.arg.PlayerReference;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.command.annotations.CommandExecutor;
import vc.api.VcApi;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ForkJoinPool;

public class SeenCommand extends Command {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final VcApi api;
    public SeenCommand(final VcApi api) {
        super("vcseen", "Gets when a player was first and last seen on 2b2t");
        this.api = api;
    }

    @CommandExecutor
    @CommandExecutor.Argument({"player"})
    private String seenPlayerName(final PlayerReference player) {
        ForkJoinPool.commonPool().execute(() -> {
            var apiResponse = this.api.getSeen(player);
            if (apiResponse.isEmpty()) {
                ChatUtils.print("Error: " + player.name() + " not found!");
            }
            String out = "";
            out += apiResponse.map(seenResponse -> "\nFirst seen: " + getSeenString(seenResponse.firstSeen())).orElse("");
            out += apiResponse.map(seenResponse -> "\nLast seen: " + getSeenString(seenResponse.lastSeen())).orElse("");
            ChatUtils.print(out);
        });
        return null;
    }

    private String getSeenString(@Nullable final OffsetDateTime time) {
        return time != null ? time.format(formatter) : "Never";
    }
}
