package com.snbnk.documentation.api.dto;

import com.snbnk.documentation.domain.DocumentStatus;
import com.snbnk.documentation.domain.DocumentType;

public record DocumentResponse(
        String id,
        String applicationId,
        String type,
        String fileName,
        String status
) {}
