package com.snbnk.onboarding.service;

import com.snbnk.onboarding.api.dto.CreateApplicationRequest;
import com.snbnk.onboarding.domain.Status;
import com.snbnk.onboarding.persistence.ApplicationEntity;
import com.snbnk.onboarding.persistence.ApplicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    @Mock
    private ApplicationRepository repository;

    @InjectMocks
    private OnboardingService service;

    @Test
    void shouldCreateApplicationWithInitiatedStatus() {
        when(repository.existsByEmail("john@test.com")).thenReturn(false);

        when(repository.save(any(ApplicationEntity.class))).thenAnswer(invocation -> {
            ApplicationEntity e = invocation.getArgument(0);
            // simulate DB-generated ID - using reflection
            // because we don't have setters for this class
            ReflectionTestUtils.setField(e, "id", "123");
            return e;
        });

        var req = new CreateApplicationRequest("John", "Doe", "john@test.com");
        var resp = service.create(req);

        assertEquals("123", resp.id());
        assertEquals("INITIATED", resp.status());
        assertEquals("john@test.com", resp.email());

        verify(repository).existsByEmail("john@test.com");
        verify(repository).save(any(ApplicationEntity.class));
    }

    @Test
    void shouldThrowConflictWhenEmailExists() {
        when(repository.existsByEmail("john@test.com")).thenReturn(true);

        var req = new CreateApplicationRequest("John", "Doe", "john@test.com");

        assertThrows(RuntimeException.class, () -> service.create(req)); // replace with ConflictException

        verify(repository).existsByEmail("john@test.com");
        verify(repository, never()).save(any());
    }

    @Test
    void shouldUpdateStatus() {

        ApplicationEntity entity =
                ApplicationEntity.create("John","Doe","john@test.com");

        ReflectionTestUtils.setField(entity, "id", "123");

        when(repository.findById("123")).thenReturn(Optional.of(entity));

        var resp = service.updateStatus("123", Status.DOCUMENT_PENDING);

        assertEquals("DOCUMENT_PENDING", resp.status());
    }
}