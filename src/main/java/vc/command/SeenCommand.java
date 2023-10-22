package vc.command;

import org.rusherhack.client.api.feature.command.Command;
import org.rusherhack.client.api.feature.command.arg.PlayerReference;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.command.annotations.CommandExecutor;
import vc.api.VcApi;
import vc.api.model.SeenResponse;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

public class SeenCommand extends Command {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final VcApi api;
    public SeenCommand(final VcApi api) {
        super("seenvc", "Gets when a player was first and last seen on 2b2t");
        this.api = api;
    }

    @CommandExecutor
    @CommandExecutor.Argument({"player"})
    private String seenPlayerName(final PlayerReference player) {
        ForkJoinPool.commonPool().execute(() -> {
            Optional<SeenResponse> firstSeen = this.api.getFirstSeen(player);
            Optional<SeenResponse> lastSeen = this.api.getLastSeen(player);
            if (firstSeen.isEmpty() && lastSeen.isEmpty()) {
                ChatUtils.print("Error: " + player.name() + " not found!");
            }
            String out = "";
            out += firstSeen.map(seenResponse -> "\nFirst seen: " + seenResponse.time().format(formatter)).orElse("");
            out += lastSeen.map(seenResponse -> "\nLast seen: " + seenResponse.time().format(formatter)).orElse("");
            ChatUtils.print(out);
        });
        return null;
    }
}
