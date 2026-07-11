package me.xjanua.spring.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.xjanua.spring.backend.model.ClientAppScope;

@Repository
public interface ClientAppScopeRepository extends JpaRepository<ClientAppScope, UUID> {

    List<ClientAppScope> findByClientAppId(UUID clientAppId);

    void deleteByClientAppId(UUID clientAppId);

    boolean existsByClientAppIdAndScopeId(UUID clientAppId, UUID scopeId);
}
