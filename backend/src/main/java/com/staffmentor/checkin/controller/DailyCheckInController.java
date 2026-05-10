package com.staffmentor.checkin.controller;

import com.staffmentor.checkin.dto.DailyCheckInRequest;
import com.staffmentor.checkin.dto.DailyCheckInResponse;
import com.staffmentor.checkin.service.DailyCheckInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkins")
@RequiredArgsConstructor
public class DailyCheckInController {
    private final DailyCheckInService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DailyCheckInResponse create(@Valid @RequestBody DailyCheckInRequest request) {
        return service.create(request);
    }

    @GetMapping("/latest")
    public DailyCheckInResponse latest() {
        return service.latest();
    }
}
