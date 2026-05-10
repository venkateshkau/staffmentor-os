package com.staffmentor.ai.client;

public interface AiClient {
    String generate(String systemPrompt, String userPrompt);
    String modelName();
}
