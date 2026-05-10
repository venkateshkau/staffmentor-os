package com.staffmentor.checkin.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "daily_checkins")
public class DailyCheckIn {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private LocalDate checkinDate;

    @Column(columnDefinition = "TEXT")
    private String studiedYesterday;

    @Column(nullable = false)
    private Integer availableMinutes;

    @Column(nullable = false)
    private Integer energyLevel;

    @Column(columnDefinition = "TEXT")
    private String blockers;

    @Column(columnDefinition = "TEXT")
    private String upcomingInterviews;

    private String priorityGoal;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (checkinDate == null) checkinDate = LocalDate.now();
        createdAt = LocalDateTime.now();
    }
}
