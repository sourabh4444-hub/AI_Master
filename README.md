# Agent Chatbot

Spring Boot 3.5, Spring AI, OpenAI, and Resilience4j chatbot using Java 21.

## Run

Set the API key in the environment before starting:

```powershell
$env:OPENAI_API_KEY = "your-api-key"
mvn spring-boot:run
```

The default model is `gpt-4o-mini`. Override it with `OPENAI_MODEL`.

## Chat request

```http
POST http://localhost:8080/api/chat
Content-Type: application/json

{"message":"Explain Java records in one paragraph."}
```

The response includes the assistant message and token usage when the provider returns usage metadata. Request outcomes, latency, model, and usage are retained by the in-memory tracker for the current process.

Resilience4j settings are in `src/main/resources/application.yml`. They can be overridden with `OPENAI_REQUESTS_PER_PERIOD`, `OPENAI_RATE_LIMIT_PERIOD`, and `OPENAI_TIMEOUT`.
