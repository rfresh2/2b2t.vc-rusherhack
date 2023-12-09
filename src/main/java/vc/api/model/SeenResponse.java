package vc.api.model;

import java.time.OffsetDateTime;

public record SeenResponse(OffsetDateTime firstSeen, OffsetDateTime lastSeen) { }
