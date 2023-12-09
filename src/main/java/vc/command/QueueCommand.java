package vc.command;

import org.rusherhack.client.api.feature.command.Command;
import org.rusherhack.client.api.utils.ChatUtils;
import org.rusherhack.core.command.annotations.CommandExecutor;
import vc.api.VcApi;

import java.util.concurrent.ForkJoinPool;

public class QueueCommand extends Command {

    private final VcApi api;
    public QueueCommand(final VcApi api) {
        super("queue", "2b2t queue status");
        this.api = api;
        addAliases("q");
    }

    @CommandExecutor
    private String getQueueStatus() {
        ForkJoinPool.commonPool().execute(() -> {
            var queueStatus = this.api.getQueueStatus();
            var out = queueStatus.map(qs ->
                                "Regular: " + qs.regular() + " [ETA: " + getQueueEta(qs.regular()) + "]"
                                    + "\nPrio: " + qs.prio())
                .orElse("Error: Failed to get queue status!");
            ChatUtils.print(out);
        });
        return null;
    }

    public static long getQueueWait(final int queuePos) {
        return (long) (12.7 * (Math.pow(queuePos, 1.28)));
    }

    public static String getEtaStringFromSeconds(final long totalSeconds) {
        final int hour = (int) (totalSeconds / 3600);
        final int minutes = (int) ((totalSeconds / 60) % 60);
        final int seconds = (int) (totalSeconds % 60);
        final String hourStr = hour >= 10 ? "" + hour : "0" + hour;
        final String minutesStr = minutes >= 10 ? "" + minutes : "0" + minutes;
        final String secondsStr = seconds >= 10 ? "" + seconds : "0" + seconds;
        return hourStr + ":" + minutesStr + ":" + secondsStr;
    }

    public static String getQueueEta(final int queuePos) {
        return getEtaStringFromSeconds(getQueueWait(queuePos));
    }
}
