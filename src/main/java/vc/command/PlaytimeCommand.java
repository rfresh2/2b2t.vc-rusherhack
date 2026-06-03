package vc.command;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import org.rusherhack.client.api.feature.command.Command;
import org.rusherhack.client.api.feature.command.arg.PlayerReference;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.command.annotations.CommandExecutor;
import vc.api.VcApi;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.ForkJoinPool;

import static vc.util.FormatUtil.formatDuration;

public class PlaytimeCommand extends Command {
    private final VcApi api;

    public PlaytimeCommand(final VcApi api) {
        super("playtime", "Gets a player's 2b2t playtime");
        this.api = api;
        addAliases("pt");
    }

    @CommandExecutor
    @CommandExecutor.Argument({"player"})
    public String playtime(final PlayerReference player) {
        ForkJoinPool.commonPool().execute(() -> {
            var playtime = this.api.getPlaytime(player);
            if (playtime.isEmpty()) {
                ChatUtils.print("Error: " + player.name() + " not found!");
                return;
            }
            var result = Component.empty()
                .append(Component.literal("Playtime")
                    .withStyle(Style.EMPTY
                        .withBold(true)))
                .append(Component.literal("\nPlayer: ")
                    .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(ChatFormatting.GRAY)))
                .append(Component.literal(player.name())
                    .withStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent.OpenUrl(URI.create("https://namemc.com/profile/" + player.name())))
                        .withHoverEvent(new HoverEvent.ShowText(Component.literal("Click to view profile")))))
                .append(Component.literal("\nPlaytime: ")
                    .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(ChatFormatting.GRAY)))
                .append(Component.literal(formatDuration(Duration.ofSeconds(playtime.get().playtimeSeconds()))));
            ChatUtils.print(result);
        });
        return null;
    }
}
