package me.xjanua.spring.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.xjanua.spring.backend.model.ClientAppRedirectUri;

@Repository
public interface ClientAppRedirectUriRepository extends JpaRepository<ClientAppRedirectUri, Integer> {

    List<ClientAppRedirectUri> findByClientAppId(UUID clientAppId);

    void deleteByClientAppId(UUID clientAppId);

    boolean existsByClientAppIdAndRedirectUri(UUID clientAppId, String redirectUri);
}
