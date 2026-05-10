package com.staffmentor.ai.prompt;

import com.staffmentor.checkin.entity.DailyCheckIn;
import com.staffmentor.goal.entity.Goal;
import com.staffmentor.skill.entity.Skill;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudyPlanPromptBuilder {
    public String systemPrompt() {
        return """
                You are StaffMentor OS, a Staff+ backend engineering mentor.
                Generate a practical study plan for today.
                Focus on backend engineering, system design, Java, Spring Boot, AWS, Kafka, Kubernetes, AI engineering, interview readiness, and portfolio execution.
                Do not produce generic advice.
                Push the user toward Staff+ thinking: reliability, scalability, observability, security, cost, maintainability, operability, and team impact.
                Return strict JSON only with these fields:
                mainTopic, whyItMatters, studyTask, codingTask, staffReflectionQuestion, expectedOutput, suggestedCalendarBlock, estimatedMinutes.
                """;
    }

    public String userPrompt(DailyCheckIn checkIn, List<Goal> goals, List<Skill> weakSkills) {
        return """
                Daily check-in:
                - studiedYesterday: %s
                - availableMinutes: %d
                - energyLevel: %d/5
                - blockers: %s
                - upcomingInterviews: %s
                - priorityGoal: %s

                Active goals:
                %s

                Weak/stale skills:
                %s

                Generate one realistic plan for today. Keep it executable.
                """.formatted(
                nullToEmpty(checkIn.getStudiedYesterday()),
                checkIn.getAvailableMinutes(),
                checkIn.getEnergyLevel(),
                nullToEmpty(checkIn.getBlockers()),
                nullToEmpty(checkIn.getUpcomingInterviews()),
                nullToEmpty(checkIn.getPriorityGoal()),
                formatGoals(goals),
                formatSkills(weakSkills)
        );
    }

    private String formatGoals(List<Goal> goals) {
        if (goals.isEmpty()) return "- No active goals yet";
        return goals.stream()
                .map(goal -> "- %s | priority=%d | target=%s | notes=%s".formatted(goal.getTitle(), goal.getPriority(), goal.getTargetDate(), nullToEmpty(goal.getDescription())))
                .toList()
                .toString();
    }

    private String formatSkills(List<Skill> skills) {
        if (skills.isEmpty()) return "- No skills tracked yet";
        return skills.stream()
                .map(skill -> "- %s | current=%d | target=%d | confidence=%d | lastPracticed=%s".formatted(skill.getName(), skill.getCurrentLevel(), skill.getTargetLevel(), skill.getConfidenceScore(), skill.getLastPracticedDate()))
                .toList()
                .toString();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
