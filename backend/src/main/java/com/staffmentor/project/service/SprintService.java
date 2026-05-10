package com.staffmentor.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.staffmentor.ai.client.AiClient;
import com.staffmentor.goal.entity.Goal;
import com.staffmentor.goal.entity.GoalStatus;
import com.staffmentor.goal.repository.GoalRepository;
import com.staffmentor.project.dto.AiSprintEvaluationResponse;
import com.staffmentor.project.dto.SprintRequest;
import com.staffmentor.project.dto.SprintResponse;
import com.staffmentor.project.entity.Sprint;
import com.staffmentor.project.entity.SprintStatus;
import com.staffmentor.project.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final AiClient aiClient;
    private final ObjectMapper objectMapper;
    private final SprintRepository sprintRepository;
    private final GoalRepository goalRepository;

    @Transactional
    public SprintResponse analyzeAndCreateSprint(SprintRequest request) {
        List<Goal> activeGoals = goalRepository.findAll().stream()
                .filter(g -> g.getStatus() == GoalStatus.ACTIVE)
                .toList();

        String activeGoalsStr = activeGoals.stream()
                .map(g -> g.getId().toString() + ": " + g.getTitle())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("None");

        String systemPrompt = """
                You are a Staff+ engineering mentor. The user has pasted a Job Description (JD) and wants to create an Interview Sprint.
                
                JD TEXT:
                ---
                %s
                ---
                
                CURRENT ACTIVE GOALS:
                %s
                
                Evaluate the JD and the current goals. Extract the key missing skills required for this JD.
                Return ONLY a JSON object with:
                - "newGoalTitles": List<String> (A list of 2-4 new, high-priority, specific technical goals to master for this JD).
                - "pausedGoalIds": List<String> (A list of UUIDs from the CURRENT ACTIVE GOALS that are NOT relevant to this JD and should be paused to focus).
                """;

        String formattedPrompt = String.format(systemPrompt, request.jdText(), activeGoalsStr);

        AiSprintEvaluationResponse parsed;
        try {
            String aiResponse = aiClient.generate("You are an expert technical interviewer and planner. Output strict JSON.", formattedPrompt);
            parsed = objectMapper.readValue(aiResponse, AiSprintEvaluationResponse.class);
        } catch (Exception e) {
            parsed = new AiSprintEvaluationResponse(List.of("Master System Design", "Review Data Structures"), List.of());
        }

        Sprint sprint = new Sprint();
        sprint.setId(UUID.randomUUID());
        sprint.setTitle(request.title() != null ? request.title() : "Interview Sprint");
        sprint.setJdText(request.jdText());
        sprint.setTargetDate(LocalDate.now().plusWeeks(4)); // default 4 week sprint
        sprint.setStatus(SprintStatus.ACTIVE);
        
        Sprint savedSprint = sprintRepository.save(sprint);

        // Pause existing goals
        if (parsed.pausedGoalIds() != null) {
            for (UUID id : parsed.pausedGoalIds()) {
                goalRepository.findById(id).ifPresent(g -> {
                    g.setStatus(GoalStatus.PAUSED);
                    goalRepository.save(g);
                });
            }
        }

        // Create new goals
        if (parsed.newGoalTitles() != null) {
            for (String title : parsed.newGoalTitles()) {
                Goal newGoal = new Goal();
                newGoal.setId(UUID.randomUUID());
                newGoal.setTitle(title);
                newGoal.setDescription("Sprint Goal for: " + savedSprint.getTitle());
                newGoal.setStatus(GoalStatus.ACTIVE);
                newGoal.setPriority(1); // Top priority
                newGoal.setTargetDate(savedSprint.getTargetDate());
                newGoal.setSprint(savedSprint);
                goalRepository.save(newGoal);
            }
        }

        List<SprintResponse.GoalDto> sprintGoals = goalRepository.findAll().stream()
                .filter(g -> savedSprint.equals(g.getSprint()))
                .map(g -> new SprintResponse.GoalDto(g.getId(), g.getTitle(), g.getStatus().name(), g.getPriority()))
                .toList();

        return new SprintResponse(
                savedSprint.getId(),
                savedSprint.getTitle(),
                savedSprint.getTargetDate(),
                savedSprint.getStatus().name(),
                sprintGoals
        );
    }

    public List<SprintResponse> getActiveSprints() {
        return sprintRepository.findAll().stream()
                .filter(s -> s.getStatus() == SprintStatus.ACTIVE)
                .map(s -> {
                    List<SprintResponse.GoalDto> sprintGoals = goalRepository.findAll().stream()
                            .filter(g -> s.equals(g.getSprint()))
                            .map(g -> new SprintResponse.GoalDto(g.getId(), g.getTitle(), g.getStatus().name(), g.getPriority()))
                            .toList();
                    return new SprintResponse(
                            s.getId(),
                            s.getTitle(),
                            s.getTargetDate(),
                            s.getStatus().name(),
                            sprintGoals
                    );
                })
                .toList();
    }
}
