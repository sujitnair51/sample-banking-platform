package com.snbnk.onboarding.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "uploaded_documents")
@Getter
@NoArgsConstructor
public class UploadedDocumentEntity {

    @Id
    private UUID id;

    private String applicationId;

    private String documentType;

    private Instant uploadedAt;

    public static UploadedDocumentEntity create(
            String applicationId,
            String documentType,
            Instant uploadedAt
    ) {
        UploadedDocumentEntity e = new UploadedDocumentEntity();
        e.id = UUID.randomUUID();
        e.applicationId = applicationId;
        e.documentType = documentType;
        e.uploadedAt = uploadedAt;
        return e;
    }
}
