package me.xjanua.spring.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.xjanua.spring.backend.model.AuthorizationRequest;

@Repository
public interface AuthorizationRequestRepository extends JpaRepository<AuthorizationRequest, UUID> {
    Optional<AuthorizationRequest> findByRequestCode(String requestCode);

    boolean existsByRequestCode(String requestCode);
}
