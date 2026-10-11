package org.example.chat;

import java.time.Duration;
import java.time.Instant;

public record UsageRecord(
        Instant occurredAt,
        Duration latency,
        String model,
        UsageSummary usage,
        boolean successful
) {
}
