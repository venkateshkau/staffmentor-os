package com.staffmentor.study.entity;

import com.staffmentor.checkin.entity.DailyCheckIn;
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
@Table(name = "study_plans")
public class StudyPlan {
    @Id
    private UUID id;

    @Column(nullable = false)
    private LocalDate planDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkin_id")
    private DailyCheckIn checkIn;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mainTopic;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String whyItMatters;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String studyTask;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String codingTask;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String staffReflectionQuestion;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String expectedOutput;

    @Column(columnDefinition = "TEXT")
    private String suggestedCalendarBlock;

    @Column(nullable = false)
    private Integer estimatedMinutes;

    private String aiModel;

    @Column(columnDefinition = "TEXT")
    private String rawAiResponse;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (planDate == null) planDate = LocalDate.now();
        createdAt = LocalDateTime.now();
    }
}
