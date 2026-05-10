package com.staffmentor.checkin.service;

import com.staffmentor.checkin.dto.DailyCheckInRequest;
import com.staffmentor.checkin.dto.DailyCheckInResponse;
import com.staffmentor.checkin.entity.DailyCheckIn;
import com.staffmentor.checkin.repository.DailyCheckInRepository;
import com.staffmentor.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DailyCheckInService {
    private final DailyCheckInRepository repository;

    @Transactional
    public DailyCheckInResponse create(DailyCheckInRequest request) {
        LocalDate date = request.checkinDate() == null ? LocalDate.now() : request.checkinDate();
        repository.findByCheckinDate(date).ifPresent(existing -> repository.deleteById(existing.getId()));
        DailyCheckIn checkIn = DailyCheckIn.builder()
                .checkinDate(date)
                .studiedYesterday(request.studiedYesterday())
                .availableMinutes(request.availableMinutes())
                .energyLevel(request.energyLevel())
                .blockers(request.blockers())
                .upcomingInterviews(request.upcomingInterviews())
                .priorityGoal(request.priorityGoal())
                .build();
        return toResponse(repository.save(checkIn));
    }

    @Transactional(readOnly = true)
    public DailyCheckIn getLatestEntity() {
        return repository.findFirstByOrderByCheckinDateDesc()
                .orElseThrow(() -> new ResourceNotFoundException("No daily check-in found"));
    }

    @Transactional(readOnly = true)
    public DailyCheckInResponse latest() {
        return toResponse(getLatestEntity());
    }

    public DailyCheckInResponse toResponse(DailyCheckIn checkIn) {
        return new DailyCheckInResponse(checkIn.getId(), checkIn.getCheckinDate(), checkIn.getStudiedYesterday(), checkIn.getAvailableMinutes(), checkIn.getEnergyLevel(), checkIn.getBlockers(), checkIn.getUpcomingInterviews(), checkIn.getPriorityGoal(), checkIn.getCreatedAt());
    }
}
