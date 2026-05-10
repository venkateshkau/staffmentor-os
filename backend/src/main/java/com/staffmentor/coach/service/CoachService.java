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
import com.staffmentor.coach.repository.ActionItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.staffmentor.ai.prompt.PromptService;
import com.staffmentor.coach.dto.ActionItemDto;
import com.staffmentor.coach.entity.ActionItem;
import com.staffmentor.coach.dto.VerificationDto;

@Service
@RequiredArgsConstructor
public class CoachService {

    private final AiClient aiClient;
    private final ObjectMapper objectMapper;
    private final PromptService promptService;
    private final CoachSessionRepository coachSessionRepository;
    private final ActionItemRepository actionItemRepository;

    @Transactional
    public LatestCoachSessionResponse generateDailyCoaching(
            DailyCoachRequest request
    ) {

        String systemPrompt =  promptService.getActivePrompt(
                PromptFeature.DAILY_COACH,
                PromptType.SYSTEM
        );

        CoachSession yesterdaySession = coachSessionRepository.findTopByOrderByCreatedAtDesc()
                .filter(session -> !session.getCreatedAt().toLocalDate().equals(java.time.LocalDate.now()))
                .orElse(null);

        if (yesterdaySession != null && yesterdaySession.getActionItems() != null && !yesterdaySession.getActionItems().isEmpty()) {
            long completedCount = yesterdaySession.getActionItems().stream().filter(ActionItem::getCompleted).count();
            int completionPercent = (int) ((completedCount * 100) / yesterdaySession.getActionItems().size());

            if (completionPercent < 50) {
                systemPrompt += "\n\nCRITICAL CONTEXT: The user completed only " + completionPercent + "% of yesterday's tasks. YOU MUST reduce today's scope to prevent overload. Prioritize consistency over volume. Simplify the plan.";
            } else if (completionPercent == 100) {
                systemPrompt += "\n\nCRITICAL CONTEXT: The user completed 100% of yesterday's tasks. Keep the momentum going, but do not artificially inflate the workload.";
            }
        }

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

        if (request.workspacePath() != null && !request.workspacePath().trim().isEmpty()) {
            try {
                java.nio.file.Path startPath = java.nio.file.Paths.get(request.workspacePath());
                if (java.nio.file.Files.exists(startPath) && java.nio.file.Files.isDirectory(startPath)) {
                    StringBuilder contextBuilder = new StringBuilder("\n\nLOCAL WORKSPACE CONTEXT (Use this to provide highly specific, relevant starting prompts):\n");
                    try (java.util.stream.Stream<java.nio.file.Path> stream = java.nio.file.Files.walk(startPath)) {
                        stream.filter(p -> {
                            String name = p.toString();
                            return !name.contains("node_modules") && !name.contains(".git") && !name.contains("target") && !name.contains("dist")
                                    && (name.endsWith(".java") || name.endsWith(".ts") || name.endsWith(".tsx"));
                        })
                        .sorted((p1, p2) -> {
                            try {
                                return java.nio.file.Files.getLastModifiedTime(p2).compareTo(java.nio.file.Files.getLastModifiedTime(p1));
                            } catch (Exception e) { return 0; }
                        })
                        .limit(3)
                        .forEach(p -> {
                            try {
                                String content = java.nio.file.Files.readString(p);
                                contextBuilder.append("\n--- ").append(p.getFileName()).append(" ---\n");
                                contextBuilder.append(content.length() > 1000 ? content.substring(0, 1000) + "...(truncated)" : content);
                            } catch (Exception e) {
                                // ignore read errors
                            }
                        });
                    }
                    userPrompt += contextBuilder.toString();
                }
            } catch (Exception e) {
                // Ignore workspace read errors
            }
        }

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
            
            java.util.List<ActionItem> actionItems = new java.util.ArrayList<>();
            for (String itemStr : parsed.actionItems()) {
                ActionItem item = new ActionItem();
                item.setId(java.util.UUID.randomUUID());
                item.setSession(session);
                item.setDescription(itemStr);
                item.setCompleted(false);
                item.setCreatedAt(java.time.LocalDateTime.now());
                actionItems.add(item);
            }
            session.setActionItems(actionItems);

            coachSessionRepository.save(session);

            return mapToDto(session);

        } catch (Exception ex) {

            return null; // For MVP, if it fails, the frontend handles it or we could throw an exception instead of returning a hardcoded response, but let's just throw for cleaner error handling or return null.
        }
    }

    public LatestCoachSessionResponse getLatestSession() {
        return coachSessionRepository
                .findTopByOrderByCreatedAtDesc()
                .map(this::mapToDto)
                .orElse(null);
    }
    
    public LatestCoachSessionResponse getTodaySession() {
        return coachSessionRepository
                .findTopByOrderByCreatedAtDesc()
                .filter(session -> session.getCreatedAt().toLocalDate().equals(java.time.LocalDate.now()))
                .map(this::mapToDto)
                .orElse(null);
    }
    
    @Transactional
    public ActionItemDto toggleActionItemCompletion(java.util.UUID actionItemId, boolean completed) {
        ActionItem item = actionItemRepository.findById(actionItemId)
                .orElseThrow(() -> new IllegalArgumentException("Action item not found"));
        item.setCompleted(completed);
        actionItemRepository.save(item);
        return new ActionItemDto(item.getId(), item.getDescription(), item.getCompleted());
    }

    private LatestCoachSessionResponse mapToDto(CoachSession session) {
        java.util.List<ActionItemDto> actionItemDtos = session.getActionItems().stream()
                .map(item -> new ActionItemDto(item.getId(), item.getDescription(), item.getCompleted()))
                .toList();

        VerificationDto verificationDto = null;
        if (session.getVerification() != null) {
            verificationDto = new VerificationDto(
                    session.getVerification().getId(),
                    session.getVerification().getSubmissionText(),
                    session.getVerification().getMasteryLevel(),
                    session.getVerification().getConfidenceScore(),
                    session.getVerification().getAiFeedback(),
                    session.getVerification().getCreatedAt()
            );
        }

        return new LatestCoachSessionResponse(
                session.getId(),
                session.getCreatedAt(),
                session.getTodayFocus(),
                actionItemDtos,
                splitLines(session.getCalendarSuggestions()),
                session.getFollowUpQuestion(),
                session.getMotivation(),
                session.getCompleted(),
                verificationDto
        );
    }

    private java.util.List<String> splitLines(String value) {

        if (value == null || value.isBlank()) {
            return java.util.List.of();
        }

        return java.util.Arrays.stream(value.split("\n"))
                .toList();
    }
}