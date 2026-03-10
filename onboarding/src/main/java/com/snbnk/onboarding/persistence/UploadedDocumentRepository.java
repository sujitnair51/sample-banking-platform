package com.snbnk.onboarding.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UploadedDocumentRepository extends JpaRepository<UploadedDocumentEntity, UUID> {

    List<UploadedDocumentEntity> findByApplicationId(String applicationId);
}
