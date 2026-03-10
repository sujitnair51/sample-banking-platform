package com.snbnk.onboarding.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, String> {
    boolean existsByEmail(String email);
}
