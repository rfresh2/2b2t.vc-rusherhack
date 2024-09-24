package vc.command;

import net.minecraft.network.chat.Component;
import org.rusherhack.client.api.feature.command.Command;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.command.annotations.CommandExecutor;
import vc.api.VcApi;
import vc.util.Queue;

import java.time.Instant;
import java.util.concurrent.ForkJoinPool;

public class QueueCommand extends Command {
    private final VcApi api;
    private final Queue queue;

    public QueueCommand(final VcApi api, final Queue queue) {
        super("queue", "2b2t queue status");
        this.api = api;
        this.queue = queue;
        addAliases("q");
    }

    @CommandExecutor
    private String getQueueStatus() {
        ForkJoinPool.commonPool().execute(() -> {
            queue.updateQueueStatus();
            queue.updateQueueEtaEquation();
            if (queue.lastRefreshedQueueStatus == Instant.EPOCH) {
                ChatUtils.print("Error: Failed to get queue status!");
                return;
            }
            var result = Component.empty()
                .append(Component.literal("\nRegular: " + queue.queueStatus.regular()
                                              + " [ETA: " + queue.getQueueEta(queue.queueStatus.regular()) + "]"));
            ChatUtils.print(result);
        });
        return null;
    }
}
