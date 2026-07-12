package me.xjanua.spring.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.exception.BadRequestException;
import me.xjanua.spring.backend.model.ClientApp;
import me.xjanua.spring.backend.model.ClientAppScope;
import me.xjanua.spring.backend.repository.ClientAppScopeRepository;

@Service
@RequiredArgsConstructor
public class ClientAppScopeService {

    private final ClientAppScopeRepository clientAppScopeRepository;
    private final ScopeService scopeService;

    public List<ClientAppScope> findByClientAppId(UUID clientAppId) {
        return clientAppScopeRepository.findByClientAppId(clientAppId);
    }

    public boolean exists(UUID clientAppId, UUID scopeId) {
        return clientAppScopeRepository.existsByClientAppIdAndScopeId(clientAppId, scopeId);
    }

    @Transactional
    public ClientAppScope addScope(ClientApp clientApp, UUID scopeId) {
        if (clientAppScopeRepository.existsByClientAppIdAndScopeId(clientApp.getId(), scopeId)) {
            throw new BadRequestException("Scope already assigned to this client app");
        }

        ClientAppScope clientAppScope = ClientAppScope.builder()
                .clientApp(clientApp)
                .scope(scopeService.findById(scopeId))
                .build();

        return clientAppScopeRepository.save(clientAppScope);
    }

    @Transactional
    public void removeScope(ClientApp clientApp, UUID scopeId) {
        clientAppScopeRepository.findByClientAppId(clientApp.getId()).stream()
                .filter(cs -> cs.getScope().getId().equals(scopeId))
                .findFirst()
                .ifPresent(clientAppScopeRepository::delete);
    }

    @Transactional
    public void deleteByClientAppId(UUID clientAppId) {
        clientAppScopeRepository.deleteByClientAppId(clientAppId);
    }

    @Transactional
    public void replaceScopes(ClientApp clientApp, List<UUID> scopeIds) {
        clientAppScopeRepository.deleteByClientAppId(clientApp.getId());

        if (scopeIds != null && !scopeIds.isEmpty()) {
            List<UUID> existingIds = scopeService.findByIds(scopeIds).stream()
                    .map(s -> s.getId())
                    .toList();

            if (existingIds.size() != scopeIds.size()) {
                throw new BadRequestException("Some scope IDs are invalid");
            }

            for (UUID scopeId : scopeIds) {
                addScope(clientApp, scopeId);
            }
        }
    }
}
