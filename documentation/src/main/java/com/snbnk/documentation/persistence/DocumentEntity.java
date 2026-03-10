package com.snbnk.documentation.persistence;

import com.snbnk.documentation.domain.DocumentStatus;
import com.snbnk.documentation.domain.DocumentType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String applicationId;

    @Enumerated(EnumType.STRING)
    private DocumentType type;

    private String fileName;

    private String storagePath;

    @Column(nullable = false)
    private Instant uploadedAt;

    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    public static DocumentEntity create (
            String applicationId,
            DocumentType type,
            String fileName,
            String storagePath
    ) {
        DocumentEntity d = new DocumentEntity();
        d.applicationId = applicationId;
        d.fileName = fileName;
        d.type = type;
        d.storagePath = storagePath;
        d.uploadedAt = Instant.now();
        d.status = DocumentStatus.UPLOADED;

        return d;
    }

}
