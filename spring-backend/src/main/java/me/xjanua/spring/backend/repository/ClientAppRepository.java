package me.xjanua.spring.backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import me.xjanua.spring.backend.model.ClientApp;

public interface ClientAppRepository extends JpaRepository<ClientApp, UUID>, JpaSpecificationExecutor<ClientApp> {

    Optional<ClientApp> findByClientId(String clientId);

    boolean existsByClientId(String clientId);

    boolean existsByName(String name);
}
