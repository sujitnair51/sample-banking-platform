package com.snbnk.onboarding.api.dto;

public record ApplicationResponse(
      String id,
      String firstName,
      String lastName,
      String email,
      String status
) {}
