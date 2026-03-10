package com.snbnk.onboarding.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.snbnk.onboarding.event.DocumentUploadedEvent;
import com.snbnk.onboarding.persistence.ProcessedEventEntity;
import com.snbnk.onboarding.persistence.ProcessedEventRepository;
import com.snbnk.onboarding.service.OnboardingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentEventConsumer {

    private final OnboardingService onboardingService;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "document-uploaded", groupId = "onboarding-service")
    public void handle(String message) {

        try {

            DocumentUploadedEvent event =
                    objectMapper.readValue(message, DocumentUploadedEvent.class);

            log.info("Received document upload event: {}", event);

            // IDEMPOTENCY CHECK
            if (processedEventRepository.existsByEventId(event.eventId())) {

                log.info("Event already processed: {}", event.eventId());
                return;
            }

            onboardingService.handleDocumentUploaded(event);

            processedEventRepository.save(ProcessedEventEntity.create(event.eventId()));

        } catch (Exception e) {
            log.error("Failed to process document event: {}", message, e);
        }
    }
}
