package com.staffmentor.coach.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staffmentor.ai.client.AiClient;
import com.staffmentor.coach.dto.AiVerificationResponse;
import com.staffmentor.coach.dto.VerificationDto;
import com.staffmentor.coach.dto.VerificationRequest;
import com.staffmentor.coach.entity.CoachSession;
import com.staffmentor.coach.entity.MasteryLevel;
import com.staffmentor.coach.entity.Verification;
import com.staffmentor.coach.repository.CoachSessionRepository;
import com.staffmentor.coach.repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final AiClient aiClient;
    private final ObjectMapper objectMapper;
    private final CoachSessionRepository coachSessionRepository;
    private final VerificationRepository verificationRepository;
    private final com.staffmentor.coach.repository.KnowledgeSnippetRepository knowledgeSnippetRepository;

    @Transactional
    public VerificationDto verifySession(VerificationRequest request) {
        CoachSession session = coachSessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.getVerification() != null) {
            throw new IllegalStateException("Verification already exists for this session");
        }

        String systemPrompt = """
                You are a Staff+ engineering mentor.
                The user has just completed their study session with the focus: '%s'.
                Their action items were:
                %s
                
                The user has submitted the following implementation summary/notes to prove their learning:
                ---
                %s
                ---
                
                Evaluate their submission. Assess their depth of understanding, correctness, and confidence.
                Return ONLY a JSON object with the following keys:
                - "masteryLevel": String (Must be one of: "WATCHED", "UNDERSTOOD", "IMPLEMENTED", "MASTERED")
                - "confidenceScore": Integer (1 to 10)
                - "aiFeedback": String (Constructive feedback, pointing out weak areas or validating their depth)
                - "coreSnippet": String (If they demonstrated a new technical learning, summarize it into a 1-3 sentence concise flashcard fact or code snippet for their Knowledge Base. If none, return null)
                """;

        String actionItemsStr = session.getActionItems().stream()
                .map(item -> "- " + item.getDescription())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("None");

        String formattedPrompt = String.format(systemPrompt, session.getTodayFocus(), actionItemsStr, request.submissionText());

        AiVerificationResponse parsed;
        try {
            String aiResponse = aiClient.generate("You are an expert evaluator. Output strict JSON.", formattedPrompt);
            parsed = objectMapper.readValue(aiResponse, AiVerificationResponse.class);
        } catch (Exception e) {
            // Fallback for MVP if AI fails
            parsed = new AiVerificationResponse(MasteryLevel.UNDERSTOOD, 7, "Good effort, but AI evaluation failed. Keep practicing.", null);
        }

        Verification verification = new Verification();
        verification.setId(UUID.randomUUID());
        verification.setSession(session);
        verification.setSubmissionText(request.submissionText());
        verification.setMasteryLevel(parsed.masteryLevel());
        verification.setConfidenceScore(parsed.confidenceScore());
        verification.setAiFeedback(parsed.aiFeedback());
        verification.setCreatedAt(LocalDateTime.now());

        verificationRepository.save(verification);
        session.setVerification(verification);
        session.setCompleted(true);
        coachSessionRepository.save(session);
        
        if (parsed.coreSnippet() != null && !parsed.coreSnippet().trim().isEmpty() && parsed.confidenceScore() > 5) {
            com.staffmentor.coach.entity.KnowledgeSnippet snippet = new com.staffmentor.coach.entity.KnowledgeSnippet();
            snippet.setContent(parsed.coreSnippet());
            snippet.setIntervalDays(1); // start with 1 day
            snippet.setNextReviewDate(java.time.LocalDate.now().plusDays(1));
            knowledgeSnippetRepository.save(snippet);
        }

        return new VerificationDto(
                verification.getId(),
                verification.getSubmissionText(),
                verification.getMasteryLevel(),
                verification.getConfidenceScore(),
                verification.getAiFeedback(),
                verification.getCreatedAt()
        );
    }
}
