package org.example.chat;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
public class ChatService {

    private static final String SYSTEM_PROMPT = """
            You are a helpful and concise assistant.
            Answer the user's request accurately. If you are unsure, say so clearly.
            """;

    private final ChatClient chatClient;
    private final UsageTracker usageTracker;

    public ChatService(ChatClient.Builder chatClientBuilder, UsageTracker usageTracker) {
        this.chatClient = chatClientBuilder.build();
        this.usageTracker = usageTracker;
    }

    @CircuitBreaker(name = "openAi")
    @Retry(name = "openAi")
    @RateLimiter(name = "openAi")
    @TimeLimiter(name = "openAi")
    public CompletableFuture<org.springframework.ai.chat.model.ChatResponse> completeAndTrack(String message) {
        Instant startedAt = Instant.now();
        return CompletableFuture.supplyAsync(() -> chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .call()
                .chatResponse()).whenComplete((response, error) -> {
            UsageSummary usage = response == null ? null : toUsageSummary(response.getMetadata().getUsage());
            usageTracker.record(new UsageRecord(
                    startedAt,
                    Duration.between(startedAt, Instant.now()),
                    response == null ? null : response.getMetadata().getModel(),
                    usage,
                    error == null
            ));
        });
    }

    private UsageSummary toUsageSummary(Usage usage) {
        if (usage == null) {
            return null;
        }
        return new UsageSummary(
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getTotalTokens()
        );
    }
}
