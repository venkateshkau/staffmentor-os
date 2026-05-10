package com.staffmentor.ai.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staffmentor.ai.client.AiClient;
import com.staffmentor.ai.dto.StudyPlanAiResponse;
import com.staffmentor.ai.prompt.StudyPlanPromptBuilder;
import com.staffmentor.checkin.entity.DailyCheckIn;
import com.staffmentor.goal.entity.Goal;
import com.staffmentor.skill.entity.Skill;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StudyPlanOrchestrator {
    private final AiClient aiClient;
    private final StudyPlanPromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public StudyPlanAiResponse generate(DailyCheckIn checkIn, List<Goal> goals, List<Skill> weakSkills) {
        String raw = aiClient.generate(promptBuilder.systemPrompt(), promptBuilder.userPrompt(checkIn, goals, weakSkills));
        try {
            StudyPlanAiResponse parsed = objectMapper.readValue(raw, StudyPlanAiResponse.class);
            return new StudyPlanAiResponse(
                    parsed.mainTopic(), parsed.whyItMatters(), parsed.studyTask(), parsed.codingTask(),
                    parsed.staffReflectionQuestion(), parsed.expectedOutput(), parsed.suggestedCalendarBlock(),
                    parsed.estimatedMinutes(), raw
            );
        } catch (Exception ex) {
            return new StudyPlanAiResponse(
                    "Review StaffMentor OS architecture",
                    "The AI response could not be parsed, so the system fell back to a safe plan. This protects reliability.",
                    "Review today's goals and weak skills, then write a 5-point architecture note.",
                    "Implement one backend improvement with validation and tests.",
                    "How should this system fail gracefully when an AI dependency is unavailable?",
                    "One code commit and one architecture note.",
                    "60-90 minutes focused work",
                    90,
                    raw
            );
        }
    }

    public String modelName() {
        return aiClient.modelName();
    }
}
