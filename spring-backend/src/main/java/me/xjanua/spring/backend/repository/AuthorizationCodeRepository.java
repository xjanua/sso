package me.xjanua.spring.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.xjanua.spring.backend.model.AuthorizationCode;

@Repository
public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode, UUID> {
    Optional<AuthorizationCode> findByCode(String code);

    boolean existsByCode(String code);
}
