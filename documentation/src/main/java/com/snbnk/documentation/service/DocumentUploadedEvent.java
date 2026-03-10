package com.snbnk.documentation.service;

import java.time.Instant;

public record DocumentUploadedEvent(
        String eventId,
        String documentId,
        String applicationId,
        String documentType,
        Instant uploadedAt
) {}
