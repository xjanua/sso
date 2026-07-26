package me.xjanua.spring.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.xjanua.spring.backend.model.UserConsent;

@Repository
public interface UserConsentRepository extends JpaRepository<UserConsent, UUID> {
    Optional<UserConsent> findByUserIdAndClientAppId(UUID userId, UUID clientAppId);
}
