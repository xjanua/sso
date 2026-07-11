package me.xjanua.spring.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.PaginationDTO;
import me.xjanua.spring.backend.dto.clientApp.ClientAppRequest;
import me.xjanua.spring.backend.dto.clientApp.ClientAppResponse;
import me.xjanua.spring.backend.dto.clientApp.ClientAppUpdateRequest;
import me.xjanua.spring.backend.enums.ClientAppStatus;
import me.xjanua.spring.backend.exception.BadRequestException;
import me.xjanua.spring.backend.exception.NotFoundException;
import me.xjanua.spring.backend.mapper.ClientAppMapper;
import me.xjanua.spring.backend.model.ClientApp;
import me.xjanua.spring.backend.model.ClientAppRedirectUri;
import me.xjanua.spring.backend.model.ClientAppScope;
import me.xjanua.spring.backend.model.Scope;
import me.xjanua.spring.backend.repository.ClientAppRedirectUriRepository;
import me.xjanua.spring.backend.repository.ClientAppRepository;
import me.xjanua.spring.backend.repository.ClientAppScopeRepository;
import me.xjanua.spring.backend.repository.ScopeRepository;
import me.xjanua.spring.backend.util.PaginationUtil;

@Service
@RequiredArgsConstructor
public class ClientAppService {

    private final ClientAppRepository clientAppRepository;
    private final ClientAppScopeRepository clientAppScopeRepository;
    private final ClientAppRedirectUriRepository clientAppRedirectUriRepository;
    private final ScopeRepository scopeRepository;
    private final ClientAppMapper clientAppMapper;

    public ClientApp findById(UUID id) {
        return clientAppRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ClientApp not found"));
    }

    public ClientApp findByClientId(String clientId) {
        return clientAppRepository.findByClientId(clientId)
                .orElseThrow(() -> new NotFoundException("ClientApp not found with clientId: " + clientId));
    }

    public ClientApp save(ClientApp clientApp) {
        return clientAppRepository.save(clientApp);
    }

    public PaginationDTO.Response fetchAll(Specification<ClientApp> spec, Pageable pageable) {
        Page<ClientApp> clientApps = clientAppRepository.findAll(spec, pageable);

        PaginationDTO.Info info = PaginationUtil.buildInfo(clientApps, pageable);

        List<ClientAppResponse> responses = clientApps.getContent().stream()
                .map(clientAppMapper::toResponse)
                .collect(Collectors.toList());

        PaginationDTO.Response response = new PaginationDTO.Response();
        response.setInfo(info);
        response.setResponse(responses);

        return response;
    }

    @Transactional
    public ClientApp create(ClientAppRequest request) {
        if (clientAppRepository.existsByName(request.getName())) {
            throw new BadRequestException("ClientApp with this name already exists");
        }

        ClientApp clientApp = ClientApp.builder()
                .clientId(generateClientId())
                .clientSecret(generateClientSecret())
                .name(request.getName())
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .status(ClientAppStatus.ACTIVE)
                .build();

        clientApp = save(clientApp);

        if (request.getScopeIds() != null && !request.getScopeIds().isEmpty()) {
            addScopesToClientApp(clientApp, request.getScopeIds());
        }

        if (request.getRedirectUris() != null && !request.getRedirectUris().isEmpty()) {
            addRedirectUrisToClientApp(clientApp, request.getRedirectUris());
        }

        return findById(clientApp.getId());
    }

    @Transactional
    public ClientApp update(UUID id, ClientAppUpdateRequest request) {
        ClientApp clientApp = findById(id);

        if (request.getName() != null && !request.getName().equals(clientApp.getName())) {
            if (clientAppRepository.existsByName(request.getName())) {
                throw new BadRequestException("ClientApp with this name already exists");
            }
            clientApp.setName(request.getName());
        }

        if (request.getDescription() != null) {
            clientApp.setDescription(request.getDescription());
        }

        if (request.getLogoUrl() != null) {
            clientApp.setLogoUrl(request.getLogoUrl());
        }

        if (request.getStatus() != null) {
            clientApp.setStatus(ClientAppStatus.valueOf(request.getStatus().toUpperCase()));
        }

        save(clientApp);

        if (request.getScopeIds() != null) {
            updateScopes(clientApp, request.getScopeIds());
        }

        if (request.getRedirectUris() != null) {
            updateRedirectUris(clientApp, request.getRedirectUris());
        }

        return findById(clientApp.getId());
    }

    @Transactional
    public void delete(UUID id) {
        ClientApp clientApp = findById(id);
        clientAppScopeRepository.deleteByClientAppId(id);
        clientAppRedirectUriRepository.deleteByClientAppId(id);
        clientAppRepository.delete(clientApp);
    }

    @Transactional
    public ClientApp regenerateSecret(UUID id) {
        ClientApp clientApp = findById(id);
        clientApp.setClientSecret(generateClientSecret());
        return save(clientApp);
    }

    @Transactional
    public ClientApp addScopes(UUID id, List<UUID> scopeIds) {
        ClientApp clientApp = findById(id);
        addScopesToClientApp(clientApp, scopeIds);
        return clientApp;
    }

    @Transactional
    public ClientApp removeScope(UUID id, UUID scopeId) {
        ClientApp clientApp = findById(id);

        List<ClientAppScope> scopes = clientAppScopeRepository.findByClientAppId(id);
        scopes.stream()
                .filter(cs -> cs.getScope().getId().equals(scopeId))
                .findFirst()
                .ifPresent(clientAppScopeRepository::delete);

        return clientApp;
    }

    @Transactional
    public ClientApp addRedirectUri(UUID id, String redirectUri) {
        ClientApp clientApp = findById(id);
        addRedirectUriToClientApp(clientApp, redirectUri);
        return findById(id);
    }

    @Transactional
    public ClientApp removeRedirectUri(UUID id, String redirectUri) {
        ClientApp clientApp = findById(id);

        List<ClientAppRedirectUri> uris = clientAppRedirectUriRepository.findByClientAppId(id);
        uris.stream()
                .filter(uri -> uri.getRedirectUri().equals(redirectUri))
                .findFirst()
                .ifPresent(clientAppRedirectUriRepository::delete);

        return findById(id);
    }

    private void addScopesToClientApp(ClientApp clientApp, List<UUID> scopeIds) {
        List<Scope> scopes = scopeRepository.findAllById(scopeIds);
        if (scopes.size() != scopeIds.size()) {
            throw new BadRequestException("Some scope IDs are invalid");
        }

        for (Scope scope : scopes) {
            if (!clientAppScopeRepository.existsByClientAppIdAndScopeId(clientApp.getId(), scope.getId())) {
                ClientAppScope clientAppScope = ClientAppScope.builder()
                        .clientApp(clientApp)
                        .scope(scope)
                        .build();
                clientAppScopeRepository.save(clientAppScope);
            }
        }
    }

    private void addRedirectUrisToClientApp(ClientApp clientApp, List<String> redirectUris) {
        for (String uri : redirectUris) {
            addRedirectUriToClientApp(clientApp, uri);
        }
    }

    private void addRedirectUriToClientApp(ClientApp clientApp, String redirectUri) {
        if (!clientAppRedirectUriRepository.existsByClientAppIdAndRedirectUri(clientApp.getId(), redirectUri)) {
            ClientAppRedirectUri appRedirectUri = ClientAppRedirectUri.builder()
                    .clientApp(clientApp)
                    .redirectUri(redirectUri)
                    .build();
            clientAppRedirectUriRepository.save(appRedirectUri);
        }
    }

    private void updateScopes(ClientApp clientApp, List<UUID> scopeIds) {
        clientAppScopeRepository.deleteByClientAppId(clientApp.getId());
        if (scopeIds != null && !scopeIds.isEmpty()) {
            addScopesToClientApp(clientApp, scopeIds);
        }
    }

    private void updateRedirectUris(ClientApp clientApp, List<String> redirectUris) {
        clientAppRedirectUriRepository.deleteByClientAppId(clientApp.getId());
        if (redirectUris != null && !redirectUris.isEmpty()) {
            addRedirectUrisToClientApp(clientApp, redirectUris);
        }
    }

    private String generateClientId() {
        String clientId;
        do {
            clientId = "sso_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        } while (clientAppRepository.existsByClientId(clientId));
        return clientId;
    }

    private String generateClientSecret() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString().replace("-", "");
    }
}
