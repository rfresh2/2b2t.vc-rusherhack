package vc.hud;

import org.rusherhack.client.api.feature.hud.TextHudElement;
import org.rusherhack.core.setting.BooleanSetting;
import vc.api.VcApi;
import vc.api.model.QueueStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Queue2b2tHudElement extends TextHudElement {

    private QueueStatus queueStatus = new QueueStatus(OffsetDateTime.now(), 0, 0);
    private long lastRefreshedEpochS = 0L;
    private final VcApi api;
    final BooleanSetting showPrio = new BooleanSetting("Show Prio", true);
    final BooleanSetting showRegular = new BooleanSetting("Show Regular", true);
    final BooleanSetting showUpdatedTime = new BooleanSetting("Show Updated Time", false);
    final BooleanSetting commaDelimiter = new BooleanSetting("Comma Delimiter", true);
    // todo: can't insert newline chars into TextHud output currently
//    final BooleanSetting multiLine = new BooleanSetting("MultiLine", false);

    public Queue2b2tHudElement(final VcApi api) {
        super("2b2t Queue");
        this.api = api;
        registerSettings(showPrio, showRegular, showUpdatedTime, commaDelimiter);
    }

    @Override
    public String getText() {
        // refresh every 5 mins in the background
        if (Instant.now().getEpochSecond() - lastRefreshedEpochS > 300L)
            refreshQueueStatus();
        String regular = null;
        String prio = null;
        String updated = null;
        if (showRegular.getValue()) {
            regular = "Regular: " + queueStatus.regular();
        }
        if (showPrio.getValue()) {
            prio = "Prio: " + queueStatus.prio();
        }
        if (showUpdatedTime.getValue()) {
            updated = "Updated " + formatDuration(Duration.between(queueStatus.time().toInstant(), Instant.now()));
        }
        return Stream.of(regular, prio, updated)
            .filter(Objects::nonNull)
            .collect(Collectors.joining((commaDelimiter.getValue() ? ", " : " ")));
    }

    private void refreshQueueStatus() {
        lastRefreshedEpochS = Instant.now().getEpochSecond();
        ForkJoinPool.commonPool().execute(() -> {
            Optional<QueueStatus> status = this.api.getQueueStatus();
            if (status.isPresent()) {
                queueStatus = status.get();
            } else {
                getLogger().error("Failed refreshing 2b2t queue!");
            }
        });
    }

    public static String formatDuration(Duration duration) {
        final StringBuilder sb = new StringBuilder();
        if (duration.toDaysPart() > 0) sb.append(duration.toDaysPart()).append("d ");
        if (duration.toHoursPart() > 0) sb.append(duration.toHoursPart()).append("h ");
        if (duration.toMinutesPart() > 0) sb.append(duration.toMinutesPart()).append("m ");
        sb.append(duration.toSecondsPart()).append("s");
        sb.append(" ago");
        return sb.toString();
    }
}
