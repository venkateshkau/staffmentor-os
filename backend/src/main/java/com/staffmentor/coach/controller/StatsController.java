package com.staffmentor.coach.controller;

import com.staffmentor.coach.entity.ActionItem;
import com.staffmentor.coach.entity.CoachSession;
import com.staffmentor.coach.repository.CoachSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final CoachSessionRepository coachSessionRepository;

    public record DailyVelocity(LocalDate date, int completionPercentage, int focusedMinutes) {}

    @GetMapping("/velocity")
    public Map<String, DailyVelocity> getVelocityHeatmap() {
        List<CoachSession> sessions = coachSessionRepository.findAll();
        Map<String, DailyVelocity> heatmap = new HashMap<>();

        for (CoachSession session : sessions) {
            LocalDate date = session.getCreatedAt().toLocalDate();
            
            int totalItems = session.getActionItems().size();
            int completedItems = (int) session.getActionItems().stream().filter(ActionItem::getCompleted).count();
            int completionPercentage = totalItems > 0 ? (completedItems * 100) / totalItems : 0;
            
            // Note: Focus minutes could be aggregated here too, but we'll leave it as 0 for this MVP unless joined
            heatmap.put(date.toString(), new DailyVelocity(date, completionPercentage, 0));
        }

        return heatmap;
    }
}
