package com.snbnk.onboarding.event;

import java.time.Instant;
import java.util.UUID;

public record DocumentUploadedEvent(
        String eventId,
        UUID documentId,
        UUID  applicationId,
        String documentType,
        Instant uploadedAt
) {}
