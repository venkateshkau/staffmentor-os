package com.staffmentor.checkin.repository;

import com.staffmentor.checkin.entity.DailyCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface DailyCheckInRepository extends JpaRepository<DailyCheckIn, UUID> {
    Optional<DailyCheckIn> findFirstByOrderByCheckinDateDesc();
    Optional<DailyCheckIn> findByCheckinDate(LocalDate checkinDate);
}
