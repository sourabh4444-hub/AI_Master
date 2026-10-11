package org.example.chat;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestControllerAdvice
public class ChatExceptionHandler {

    @ExceptionHandler({RequestNotPermitted.class, CallNotPermittedException.class, BulkheadFullException.class})
    public ResponseEntity<Map<String, String>> handleRateLimitOrUnavailable() {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("error", "The chat service is busy or rate limited. Please try again later."));
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<Map<String, String>> handleTimeout() {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body(Map.of("error", "The OpenAI request timed out."));
    }
}
