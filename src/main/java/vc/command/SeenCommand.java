package vc.command;

import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import org.rusherhack.client.api.feature.command.Command;
import org.rusherhack.client.api.feature.command.arg.PlayerReference;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.command.annotations.CommandExecutor;
import vc.api.VcApi;

import java.util.concurrent.ForkJoinPool;

import static vc.util.FormatUtil.getSeenString;

public class SeenCommand extends Command {
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
                return;
            }
            var result = Component.empty()
                .append(Component.literal("\nSeen")
                            .withStyle(Style.EMPTY
                                           .withBold(true)))
                .append(Component.literal("\nPlayer")
                            .withStyle(Style.EMPTY
                                           .withBold(true)
                            ))
                .append(Component.literal("\n" + player.name())
                            .withStyle(Style.EMPTY
                                           .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://namemc.com/profile/" + player.name()))
                                           .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to view profile")))))
                .append(Component.literal("\nFirst Seen")
                            .withStyle(Style.EMPTY
                                           .withBold(true)))
                .append(Component.literal("\n" + getSeenString(apiResponse.get().firstSeen())))
                .append(Component.literal("\nLast Seen")
                            .withStyle(Style.EMPTY
                                           .withBold(true)))
                .append(Component.literal("\n" + getSeenString(apiResponse.get().lastSeen())));
            ChatUtils.print(result);
        });
        return null;
    }
}
