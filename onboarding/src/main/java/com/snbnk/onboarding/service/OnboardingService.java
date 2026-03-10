package com.snbnk.onboarding.service;

import com.snbnk.onboarding.api.dto.ApplicationResponse;
import com.snbnk.onboarding.api.dto.CreateApplicationRequest;
import com.snbnk.onboarding.domain.RequiredDocuments;
import com.snbnk.onboarding.domain.Status;
import com.snbnk.onboarding.error.ConflictException;
import com.snbnk.onboarding.event.DocumentUploadedEvent;
import com.snbnk.onboarding.persistence.ApplicationEntity;
import com.snbnk.onboarding.persistence.ApplicationRepository;
import com.snbnk.onboarding.persistence.UploadedDocumentEntity;
import com.snbnk.onboarding.persistence.UploadedDocumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final ApplicationRepository repository;
    private final UploadedDocumentRepository uploadedDocumentRepository;

    public ApplicationResponse create(CreateApplicationRequest req) {
        if(repository.existsByEmail(req.email())){
            throw new ConflictException("Email already exists: " + req.email());
        }

        ApplicationEntity e = ApplicationEntity.create(
                req.firstName(),
                req.lastName(),
                req.email()
        );

        ApplicationEntity saved = repository.save(e);

        return toResponse(saved);
    }

    public List<ApplicationResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

// No repository.save() needed — JPA dirty checking will update it.
    @Transactional
    public ApplicationResponse updateStatus(String id, Status status) {
        ApplicationEntity app = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        app.transitionTo(status);

        return toResponse(app);
    }

    private ApplicationResponse toResponse(ApplicationEntity e) {
        return new ApplicationResponse(
                e.getId(),
                e.getFirstName(),
                e.getLastName(),
                e.getEmail(),
                e.getStatus().name()
        );
    }

    @Transactional
    public void evaluateDocumentStatus(String applicationId) {

        log.info("start evaluateDocumentStatus - {}", applicationId);

        ApplicationEntity app =
                repository.findById(applicationId)
                        .orElseThrow(() -> new RuntimeException("Application not found"));

        List<UploadedDocumentEntity> docs =
                uploadedDocumentRepository.findByApplicationId(applicationId);

        Set<String> uploaded =
                docs.stream()
                        .map(UploadedDocumentEntity::getDocumentType)
                        .collect(Collectors.toSet());

        if (uploaded.containsAll(RequiredDocuments.REQUIRED)) {

            if(app.getStatus() != Status.UNDER_REVIEW) {
                log.info("update status -> UNDER_REVIEW");
//                updateStatus(applicationId, Status.UNDER_REVIEW);
                app.transitionTo(Status.UNDER_REVIEW);
//                We no longer call updateStatus() internally.
//                Instead we update the entity directly.
//                Because we are already inside a transaction.
//                Hibernate will persist automatically.
            }

        } else {
            if(app.getStatus() == Status.INITIATED){
                log.info("update status -> DOCUMENT_PENDING");
//                updateStatus(applicationId, Status.DOCUMENT_PENDING);
                app.transitionTo(Status.DOCUMENT_PENDING);
            }

        }
    }

    @Transactional
    public void handleDocumentUploaded(DocumentUploadedEvent event) {
        uploadedDocumentRepository.save(
                UploadedDocumentEntity.create(
                        event.applicationId().toString(),
                        event.documentType(),
                        event.uploadedAt()
                )
        );

        evaluateDocumentStatus(event.applicationId().toString());
    }
}
