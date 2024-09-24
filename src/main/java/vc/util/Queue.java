package vc.util;

import vc.api.VcApi;
import vc.api.model.QueueEtaEquation;
import vc.api.model.QueueStatus;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;

public class Queue {

    private final VcApi api;
    public QueueStatus queueStatus = new QueueStatus(OffsetDateTime.now(), 0, 0);
    public Instant lastRefreshedQueueStatus = Instant.EPOCH;
    public QueueEtaEquation queueEtaEquation = new QueueEtaEquation(343.0, 0.743);
    public Instant lastQueueEtaEquation = Instant.EPOCH;

    public Queue(final VcApi api) {
        this.api = api;
    }

    public long getQueueWait(final int queuePos) {
        return (long) (queueEtaEquation.factor() * (Math.pow(queuePos, queueEtaEquation.pow())));
    }

    public String getEtaStringFromSeconds(final long totalSeconds) {
        final int hour = (int) (totalSeconds / 3600);
        final int minutes = (int) ((totalSeconds / 60) % 60);
        final int seconds = (int) (totalSeconds % 60);
        final String hourStr = hour >= 10 ? "" + hour : "0" + hour;
        final String minutesStr = minutes >= 10 ? "" + minutes : "0" + minutes;
        final String secondsStr = seconds >= 10 ? "" + seconds : "0" + seconds;
        return hourStr + ":" + minutesStr + ":" + secondsStr;
    }

    public String getQueueEta(final int queuePos) {
        return getEtaStringFromSeconds(getQueueWait(queuePos));
    }

    public void updateQueueStatus() {
        if (lastRefreshedQueueStatus.isAfter(Instant.now().minus(Duration.ofMinutes(1)))) return;
        var status = api.getQueueStatus();
        if (status.isPresent()) {
            queueStatus = status.get();
            lastRefreshedQueueStatus = Instant.now();
        }
    }

    public void updateQueueEtaEquation() {
        if (lastQueueEtaEquation.isAfter(Instant.now().minus(Duration.ofHours(6)))) return;
        var equation = api.getQueueEtaEquation();
        if (equation.isPresent()) {
            queueEtaEquation = equation.get();
            lastQueueEtaEquation = Instant.now();
        }
    }
}
