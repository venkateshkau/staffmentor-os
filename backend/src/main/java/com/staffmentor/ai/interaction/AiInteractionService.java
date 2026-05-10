package com.staffmentor.ai.interaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiInteractionService {

    private final AiInteractionRepository repository;

    public void saveSuccess(
            String feature,
            String model,
            String systemPrompt,
            String userPrompt,
            String requestBody,
            String rawResponse,
            String parsedResponse,
            long latencyMs
    ) {
        AiInteraction interaction = new AiInteraction();

        interaction.setId(UUID.randomUUID());
        interaction.setFeature(feature);
        interaction.setModel(model);
        interaction.setSuccess(true);
        interaction.setLatencyMs(latencyMs);
        interaction.setCreatedAt(LocalDateTime.now());

        interaction.setPromptVersion("v1");

        interaction.setSystemPrompt(systemPrompt);
        interaction.setUserPrompt(userPrompt);

        interaction.setRequestBody(requestBody);
        interaction.setRawResponse(rawResponse);
        interaction.setParsedResponse(parsedResponse);

        repository.save(interaction);
    }

    public void saveFailure(
            String feature,
            String model,
            String systemPrompt,
            String userPrompt,
            String requestBody,
            String rawResponse,
            String errorMessage,
            long latencyMs
    ) {
        AiInteraction interaction = new AiInteraction();

        interaction.setId(UUID.randomUUID());
        interaction.setFeature(feature);
        interaction.setModel(model);
        interaction.setSuccess(false);
        interaction.setLatencyMs(latencyMs);
        interaction.setCreatedAt(LocalDateTime.now());

        interaction.setPromptVersion("v1");

        interaction.setSystemPrompt(systemPrompt);
        interaction.setUserPrompt(userPrompt);

        interaction.setRequestBody(requestBody);
        interaction.setRawResponse(rawResponse);
        interaction.setErrorMessage(errorMessage);

        repository.save(interaction);
    }
}