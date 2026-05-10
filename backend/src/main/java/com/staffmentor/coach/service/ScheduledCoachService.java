package com.staffmentor.coach.service;

import com.staffmentor.coach.dto.DailyCoachRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ScheduledCoachService {

    private final CoachService coachService;
    private final com.staffmentor.coach.repository.CoachSessionRepository coachSessionRepository;

    // Run at 8:00 AM every day
    @Scheduled(cron = "0 0 8 * * *")
    public void generateDailySession() {
        // Check if today already has a session
        boolean hasTodaySession = coachSessionRepository.findAll().stream()
                .anyMatch(s -> s.getCreatedAt().toLocalDate().equals(LocalDate.now()));
                
        if (hasTodaySession) return;

        // Auto-generate a plan with default values
        DailyCoachRequest autoRequest = new DailyCoachRequest(
                8, // default decent energy
                120, // default 2 hours
                true, // optimistic
                "None", 
                "", 
                "Auto-generated morning plan.",
                "" // no workspace path via CRON
        );

        coachService.generateDailyCoaching(autoRequest);
    }
}
