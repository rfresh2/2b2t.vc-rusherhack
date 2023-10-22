package vc.command;

import org.rusherhack.client.api.feature.command.Command;
import org.rusherhack.core.command.annotations.CommandExecutor;
import vc.api.VcApi;
import vc.api.model.SeenResponse;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class SeenCommand extends Command {
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final VcApi api;
    public SeenCommand(final VcApi api) {
        super("SeenCommand", "Gets when a player was first and last seen on 2b2t");
        this.api = api;
    }

    @CommandExecutor
    private void seenVc() {
    }

    @CommandExecutor
    @CommandExecutor.Argument({"string"})
    private String seenPlayerName(final String playerName) {
        Optional<SeenResponse> firstSeen = this.api.getFirstSeen(playerName);
        Optional<SeenResponse> lastSeen = this.api.getLastSeen(playerName);
        if (firstSeen.isEmpty() && lastSeen.isEmpty()) {
            return "Error: " + playerName + " not found!";
        }
        String out = "";
        out += firstSeen.map(seenResponse -> "First seen: " + seenResponse.time().format(formatter) + "\n").orElse("");
        out += lastSeen.map(seenResponse -> "Last seen: " + seenResponse.time().format(formatter)).orElse("");
        return out;
    }
}
