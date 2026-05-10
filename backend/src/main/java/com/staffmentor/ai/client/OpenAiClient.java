package com.staffmentor.ai.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.staffmentor.ai.aspect.TrackAiInteraction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class OpenAiClient implements AiClient {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public OpenAiClient(
            RestClient.Builder builder,
            ObjectMapper objectMapper,
            @Value("${staffmentor.ai.openai-api-key}") String apiKey,
            @Value("${staffmentor.ai.openai-model}") String model,
            @Value("${staffmentor.ai.base-url}") String baseUrl
    ) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    @TrackAiInteraction(feature = "STUDY_PLANNER")
    @Override
    public String generate(String systemPrompt, String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return fallbackJson();
        }

        Map<String, Object> request = Map.of(
                "model", model,
                "input", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "text", Map.of("format", Map.of("type", "json_object"))
        );

        String response = restClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);

        return extractOutputText(response);
    }

    @Override
    public String modelName() {
        return model;
    }

    private String extractOutputText(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode output = root.path("output");
            if (output.isArray()) {
                for (JsonNode item : output) {
                    JsonNode content = item.path("content");
                    if (content.isArray()) {
                        for (JsonNode c : content) {
                            String text = c.path("text").asText(null);
                            if (text != null && !text.isBlank()) return text;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // Return raw response below so parser/fallback can handle it.
        }
        return response;
    }

    private String fallbackJson() {
        return """
                {
                  "mainTopic": "Java backend fundamentals and Staff+ execution",
                  "whyItMatters": "This fallback plan appears because OPENAI_API_KEY is not configured. It still gives you a useful local workflow.",
                  "studyTask": "Review one backend concept deeply: API design, transactions, validation, and error handling.",
                  "codingTask": "Implement or improve one vertical slice in StaffMentor OS with tests and clear API contracts.",
                  "staffReflectionQuestion": "What tradeoff did I make today around reliability, maintainability, operability, or delivery speed?",
                  "expectedOutput": "One committed improvement, one written note, and one follow-up task.",
                  "suggestedCalendarBlock": "90 minutes focused deep work",
                  "estimatedMinutes": 90
                }
                """;
    }
}
