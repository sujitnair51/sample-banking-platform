package com.snbnk.onboarding.api;

import com.snbnk.onboarding.api.dto.ApplicationResponse;
import com.snbnk.onboarding.api.dto.CreateApplicationRequest;
import com.snbnk.onboarding.domain.Status;
import com.snbnk.onboarding.service.OnboardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {
    private final OnboardingService service;

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(
            @RequestBody @Valid CreateApplicationRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @GetMapping
    public List<ApplicationResponse> getAll() {
        return service.getAll();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable String id,
            @RequestParam Status status
    ){
        return ResponseEntity.ok(service.updateStatus(id, status));
    }
}
