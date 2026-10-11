package org.example.chat;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
        return chatService.completeAndTrack(request.message())
                .thenApply(response -> ResponseEntity.ok(new ChatResponse(
                        response.getResult().getOutput().getText(),
                        toUsageSummary(response)
                )));
    }

    private UsageSummary toUsageSummary(org.springframework.ai.chat.model.ChatResponse response) {
        var usage = response.getMetadata().getUsage();
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
