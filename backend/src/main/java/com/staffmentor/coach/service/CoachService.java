package com.staffmentor.coach.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staffmentor.ai.client.AiClient;
import com.staffmentor.ai.prompt.PromptFeature;
import com.staffmentor.ai.prompt.PromptType;
import com.staffmentor.coach.dto.DailyCoachRequest;
import com.staffmentor.coach.dto.DailyCoachResponse;
import com.staffmentor.coach.dto.LatestCoachSessionResponse;
import com.staffmentor.coach.entity.CoachSession;
import com.staffmentor.coach.repository.CoachSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.staffmentor.ai.prompt.PromptService;

@Service
@RequiredArgsConstructor
public class CoachService {

    private final AiClient aiClient;
    private final ObjectMapper objectMapper;
    private final PromptService promptService;
    private final CoachSessionRepository coachSessionRepository;

    public DailyCoachResponse generateDailyCoaching(
            DailyCoachRequest request
    ) {

        String systemPrompt =  promptService.getActivePrompt(
                PromptFeature.DAILY_COACH,
                PromptType.SYSTEM
        );

        String userPrompt =  promptService.getActivePrompt(
                        PromptFeature.DAILY_COACH,
                        PromptType.USER
                )
                .formatted(
                        request.energyLevel(),
                        request.availableMinutes(),
                        request.completedYesterday(),
                        request.blockers(),
                        request.upcomingInterview(),
                        request.additionalNotes()
                );

        try {

            String response = aiClient.generate(
                    systemPrompt,
                    userPrompt
            );

            DailyCoachResponse parsed =
                    objectMapper.readValue(
                            response,
                            DailyCoachResponse.class
                    );

            CoachSession session = new CoachSession();

            session.setId(java.util.UUID.randomUUID());

            session.setCreatedAt(java.time.LocalDateTime.now());

            session.setEnergyLevel(request.energyLevel());

            session.setAvailableMinutes(request.availableMinutes());

            session.setCompletedYesterday(request.completedYesterday());

            session.setBlockers(request.blockers());

            session.setUpcomingInterview(request.upcomingInterview());

            session.setAdditionalNotes(request.additionalNotes());

            session.setTodayFocus(parsed.todayFocus());

            session.setActionItems(
                    String.join("\n", parsed.actionItems())
            );

            session.setCalendarSuggestions(
                    String.join("\n", parsed.calendarSuggestions())
            );

            session.setFollowUpQuestion(
                    parsed.followUpQuestion()
            );

            session.setMotivation(
                    parsed.motivation()
            );

            session.setCompleted(false);

            coachSessionRepository.save(session);

            return parsed;

        } catch (Exception ex) {

            return new DailyCoachResponse(
                    "Focus on one high-leverage backend topic today.",
                    java.util.List.of(
                            "Study Java concurrency for 45 minutes",
                            "Implement one backend improvement",
                            "Write one engineering note"
                    ),
                    java.util.List.of(
                            "7:00 PM - 8:30 PM Deep Work"
                    ),
                    "What blocked your momentum yesterday?",
                    "Consistency beats intensity."
            );
        }
    }

    public LatestCoachSessionResponse getLatestSession() {

        return coachSessionRepository
                .findTopByOrderByCreatedAtDesc()
                .map(session ->
                        new LatestCoachSessionResponse(
                                session.getId(),
                                session.getCreatedAt(),
                                session.getTodayFocus(),
                                splitLines(session.getActionItems()),
                                splitLines(session.getCalendarSuggestions()),
                                session.getFollowUpQuestion(),
                                session.getMotivation(),
                                session.getCompleted()
                        )
                )
                .orElse(null);
    }

    private java.util.List<String> splitLines(String value) {

        if (value == null || value.isBlank()) {
            return java.util.List.of();
        }

        return java.util.Arrays.stream(value.split("\n"))
                .toList();
    }
}