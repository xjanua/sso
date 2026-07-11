package me.xjanua.spring.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import me.xjanua.spring.backend.model.Scope;

@Repository
public interface ScopeRepository extends JpaRepository<Scope, UUID>, JpaSpecificationExecutor<Scope> {

    Optional<Scope> findByCode(String code);

    List<Scope> findByCodeIn(List<String> codes);

    List<Scope> findByIsDefault(Boolean isDefault);

    @Query("SELECT s FROM Scope s JOIN ClientAppScope cas ON s.id = cas.scope.id WHERE cas.clientApp.id = :clientAppId")
    List<Scope> findByClientAppId(@Param("clientAppId") UUID clientAppId);
}
