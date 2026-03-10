package com.snbnk.documentation.service;

import com.snbnk.documentation.api.dto.DocumentResponse;
import com.snbnk.documentation.domain.DocumentType;
import com.snbnk.documentation.persistence.DocumentEntity;
import com.snbnk.documentation.persistence.DocumentRepository;
import com.snbnk.documentation.persistence.OutboxEventEntity;
import com.snbnk.documentation.persistence.OutboxEventRepository;
import com.snbnk.documentation.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository repository;
    private final FileStorageService storage;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public DocumentResponse upload(
            String applicationId,
            DocumentType type,
            MultipartFile file
    ) {

        String path = storage.store(file);

        DocumentEntity entity =
                DocumentEntity.create(
                        applicationId,
                        type,
                        file.getOriginalFilename(),
                        path
                );

        DocumentEntity saved = repository.save(entity);

        DocumentUploadedEvent event =
                new DocumentUploadedEvent(
                        UUID.randomUUID().toString(),
                        saved.getId(),
                        saved.getApplicationId(),
                        saved.getType().name(),
                        saved.getUploadedAt()
                );

        try {

            String payload = objectMapper.writeValueAsString(event);

            OutboxEventEntity outbox =
                    OutboxEventEntity.create(
                            "DOCUMENT",
                            saved.getId(),
                            "DOCUMENT_UPLOADED",
                            payload
                    );

            outboxEventRepository.save(outbox);

        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }

        return toResponse(saved);
    }

    public List<DocumentResponse> list(String applicationId) {

        return repository.findByApplicationId(applicationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private DocumentResponse toResponse(DocumentEntity e) {

        return new DocumentResponse(
                e.getId(),
                e.getApplicationId(),
                e.getType().name(),
                e.getFileName(),
                e.getStatus().name()
        );
    }
}
