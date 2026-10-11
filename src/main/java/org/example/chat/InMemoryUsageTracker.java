package org.example.chat;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryUsageTracker implements UsageTracker {

    private final AtomicLong requestCount = new AtomicLong();
    private final List<UsageRecord> records = new CopyOnWriteArrayList<>();

    @Override
    public void record(UsageRecord record) {
        records.add(record);
        requestCount.incrementAndGet();
    }

    public long requestCount() {
        return requestCount.get();
    }

    public List<UsageRecord> records() {
        return List.copyOf(records);
    }
}
