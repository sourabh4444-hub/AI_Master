package org.example.chat;

public record UsageSummary(
        Integer promptTokens,
        Integer completionTokens,
        Integer totalTokens
) {
}
