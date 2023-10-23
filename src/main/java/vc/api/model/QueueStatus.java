package vc.api.model;

import java.time.OffsetDateTime;

public record QueueStatus(OffsetDateTime time, int prio, int regular) {
}
