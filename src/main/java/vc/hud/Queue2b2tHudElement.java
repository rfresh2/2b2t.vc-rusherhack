package vc.hud;

import net.minecraft.network.chat.Component;
import org.rusherhack.client.api.feature.hud.ShortListHudElement;
import org.rusherhack.core.setting.BooleanSetting;
import vc.api.VcApi;
import vc.util.FormatUtil;
import vc.util.Queue;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;

public class Queue2b2tHudElement extends ShortListHudElement {
    private final VcApi api;
    private final Queue queue;
    private long lastRefreshedEpochS = 0L;
    final BooleanSetting showPrio = new BooleanSetting("Show Prio", true);
    final BooleanSetting showRegular = new BooleanSetting("Show Regular", true);
    final BooleanSetting showUpdatedTime = new BooleanSetting("Show Updated Time", false);

    public Queue2b2tHudElement(final VcApi api, final Queue queue) {
        super("2b2t Queue");
        this.api = api;
        this.queue = queue;
        registerSettings(showPrio, showRegular, showUpdatedTime);
    }

    private void refreshQueueStatus() {
        lastRefreshedEpochS = Instant.now().getEpochSecond();
        ForkJoinPool.commonPool().execute(queue::updateQueueStatus);
    }

    @Override
    public Component[] getComponents() {
        // refresh every 5 mins in the background
        if (Instant.now().getEpochSecond() - lastRefreshedEpochS > 300L)
            refreshQueueStatus();
        Component regular = null;
        Component prio = null;
        Component updated = null;
        if (showRegular.getValue()) {
            regular = Component.literal("Regular: " + queue.queueStatus.regular());
        }
        if (showPrio.getValue()) {
            prio = Component.literal("Prio: " + queue.queueStatus.prio());
        }
        if (showUpdatedTime.getValue()) {
            updated = Component
                .literal("Updated "
                             + FormatUtil.formatDuration(Duration.between(queue.queueStatus.time().toInstant(), Instant.now()))
                             + " ago");
        }
        return Stream.of(regular, prio, updated)
            .filter(Objects::nonNull)
            .toArray(Component[]::new);
    }
}
