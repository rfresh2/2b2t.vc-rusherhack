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
            if (statsResponse.isEmpty()) {
                ChatUtils.print("Error: " + player.name() + " not found!");
                return;
            }
            var result = Component.empty()
                .append(Component.literal("Stats")
                    .withStyle(Style.EMPTY
                        .withBold(true)))
                .append(Component.literal("\nPlayer: ")
                    .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(ChatFormatting.GRAY)
                    ))
                .append(Component.literal(player.name())
                    .withStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,
                            "https://namemc.com/profile/" + player.name()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Component.literal("Click to view profile")))))
                .append(Component.literal("\nJoins: ")
                    .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(ChatFormatting.GRAY)))
                .append(Component.literal("" + statsResponse.get().joinCount()))
                .append(Component.literal("\nLeaves: ")
                    .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(ChatFormatting.GRAY)))
                .append(Component.literal("" + statsResponse.get().leaveCount()))
                .append(Component.literal("\nFirst Seen: ")
                    .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(ChatFormatting.GRAY)))
                .append(Component.literal(getSeenString(statsResponse.get().firstSeen())))
                .append(Component.literal("\nLast Seen: ")
                    .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(ChatFormatting.GRAY)))
                .append(Component.literal(getSeenString(statsResponse.get().lastSeen())))
                .append(Component.literal("\nPlaytime: ")
                    .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(ChatFormatting.GRAY)))
                .append(Component.literal(formatDuration(Duration.ofSeconds(statsResponse.get().playtimeSeconds()))))
                .append(Component.literal("\nPlaytime (Last 30 Days): ")
                    .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(ChatFormatting.GRAY)))
                .append(Component.literal(formatDuration(Duration.ofSeconds(statsResponse.get()
                    .playtimeSecondsMonth()))))
                .append(Component.literal("\nDeaths: ")
                    .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(ChatFormatting.GRAY))
                    .append(Component.literal("" + statsResponse.get().deathCount())))
                .append(Component.literal("\nKills: ")
                    .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(ChatFormatting.GRAY)))
                .append(Component.literal("" + statsResponse.get().killCount()))
                .append(Component.literal("\nChats: ")
                    .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(ChatFormatting.GRAY)))
                .append(Component.literal("" + statsResponse.get().chatsCount()))
                .append(Component.literal("\nPrio: ")
                    .withStyle(Style.EMPTY
                        .withBold(true)
                        .withColor(ChatFormatting.GRAY)))
                .append(Component.literal(statsResponse.get().prio() ? "Yes" : "No"));
            ChatUtils.print(result);
        });
        return null;
    }
}
