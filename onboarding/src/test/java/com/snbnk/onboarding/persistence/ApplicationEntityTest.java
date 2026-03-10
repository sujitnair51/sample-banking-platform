package com.snbnk.onboarding.persistence;

import com.snbnk.onboarding.domain.Status;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationEntityTest {

    @Test
    void shouldAllowValidTransition() {

        ApplicationEntity app =
                ApplicationEntity.create("John","Doe","john@test.com");

        app.transitionTo(Status.DOCUMENT_PENDING);

        assertEquals(Status.DOCUMENT_PENDING, app.getStatus());
    }

    @Test
    void shouldRejectInvalidTransition() {

        ApplicationEntity app =
                ApplicationEntity.create("John","Doe","john@test.com");

        assertThrows(IllegalStateException.class,
                () -> app.transitionTo(Status.APPROVED));
    }
}
