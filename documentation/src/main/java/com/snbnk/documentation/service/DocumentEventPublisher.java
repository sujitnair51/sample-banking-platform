package com.snbnk.documentation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(DocumentUploadedEvent event) {
        kafkaTemplate.send(
                "document-uploaded",
                event.applicationId(),
                event
        );

    }
}
