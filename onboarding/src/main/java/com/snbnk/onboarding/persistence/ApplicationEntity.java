package com.snbnk.onboarding.persistence;

import com.snbnk.onboarding.domain.Status;
import jakarta.persistence.*;
import lombok.*;

// don't use @Data - Why: @Data generates equals/hashCode/toString (this could cause problems later)
// (along with getters/setters/Constructors for final fields)
// that can bite you badly once relationships and proxies appear.

@Getter
//@Setter - No setters = safer domain model.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "applications",
        indexes = {
                @Index(name = "idx_app_email", columnList = "email", unique = true)
        }
)
public class ApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(name = "email", nullable = false, length = 254, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private Status status;

    public static ApplicationEntity create( String firstName, String lastName, String email){
        ApplicationEntity e = new ApplicationEntity();
        e.firstName = firstName;
        e.lastName = lastName;
        e.email = email;
        e.status = Status.INITIATED;
        return e;
    }

    public void transitionTo(Status newStatus) {
        if(!isValidTransition(this.status, newStatus)){
            throw new IllegalStateException(
                    "Invalid status transition from " + status + " to " + newStatus
            );
        }

        this.status = newStatus;
    }

    private boolean isValidTransition(Status current, Status next) {

        return switch(current) {
            case INITIATED -> next == Status.DOCUMENT_PENDING;
            case DOCUMENT_PENDING -> next == Status.UNDER_REVIEW;
            case UNDER_REVIEW -> next == Status.APPROVED || next == Status.REJECTED;
            case APPROVED, REJECTED -> false;
        };
    }
}
