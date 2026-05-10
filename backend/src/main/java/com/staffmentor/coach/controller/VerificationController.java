package com.staffmentor.coach.controller;

import com.staffmentor.coach.dto.VerificationDto;
import com.staffmentor.coach.dto.VerificationRequest;
import com.staffmentor.coach.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @PostMapping("/submit")
    public VerificationDto submitVerification(@RequestBody VerificationRequest request) {
        return verificationService.verifySession(request);
    }
}
