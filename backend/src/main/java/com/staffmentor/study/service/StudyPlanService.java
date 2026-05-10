package com.staffmentor.study.service;

import com.staffmentor.ai.dto.StudyPlanAiResponse;
import com.staffmentor.ai.orchestrator.StudyPlanOrchestrator;
import com.staffmentor.checkin.entity.DailyCheckIn;
import com.staffmentor.checkin.service.DailyCheckInService;
import com.staffmentor.common.exception.ResourceNotFoundException;
import com.staffmentor.goal.entity.Goal;
import com.staffmentor.goal.service.GoalService;
import com.staffmentor.skill.entity.Skill;
import com.staffmentor.skill.service.SkillService;
import com.staffmentor.study.dto.StudyPlanResponse;
import com.staffmentor.study.entity.StudyPlan;
import com.staffmentor.study.repository.StudyPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyPlanService {
    private final StudyPlanRepository repository;
    private final DailyCheckInService checkInService;
    private final GoalService goalService;
    private final SkillService skillService;
    private final StudyPlanOrchestrator orchestrator;

    @Transactional
    public StudyPlanResponse generateTodayPlan() {
        DailyCheckIn checkIn = checkInService.getLatestEntity();
        List<Goal> goals = goalService.findActiveGoalEntities();
        List<Skill> weakSkills = skillService.findWeakSkillEntities();
        StudyPlanAiResponse ai = orchestrator.generate(checkIn, goals, weakSkills);

        StudyPlan plan = StudyPlan.builder()
                .planDate(LocalDate.now())
                .checkIn(checkIn)
                .mainTopic(ai.mainTopic())
                .whyItMatters(ai.whyItMatters())
                .studyTask(ai.studyTask())
                .codingTask(ai.codingTask())
                .staffReflectionQuestion(ai.staffReflectionQuestion())
                .expectedOutput(ai.expectedOutput())
                .suggestedCalendarBlock(ai.suggestedCalendarBlock())
                .estimatedMinutes(ai.estimatedMinutes() == null ? checkIn.getAvailableMinutes() : ai.estimatedMinutes())
                .aiModel(orchestrator.modelName())
                .rawAiResponse(ai.rawResponse())
                .build();
        return toResponse(repository.save(plan));
    }

    @Transactional(readOnly = true)
    public StudyPlanResponse today() {
        return repository.findFirstByPlanDateOrderByCreatedAtDesc(LocalDate.now())
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("No study plan found for today"));
    }

    private StudyPlanResponse toResponse(StudyPlan plan) {
        return new StudyPlanResponse(plan.getId(), plan.getPlanDate(), plan.getMainTopic(), plan.getWhyItMatters(), plan.getStudyTask(), plan.getCodingTask(), plan.getStaffReflectionQuestion(), plan.getExpectedOutput(), plan.getSuggestedCalendarBlock(), plan.getEstimatedMinutes(), plan.getAiModel(), plan.getCreatedAt());
    }
}
