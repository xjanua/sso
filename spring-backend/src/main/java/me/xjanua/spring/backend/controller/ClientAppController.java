package me.xjanua.spring.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.PaginationDTO;
import me.xjanua.spring.backend.dto.clientApp.ClientAppRequest;
import me.xjanua.spring.backend.dto.clientApp.ClientAppResponse;
import me.xjanua.spring.backend.dto.clientApp.ClientAppUpdateRequest;
import me.xjanua.spring.backend.mapper.ClientAppMapper;
import me.xjanua.spring.backend.model.ClientApp;
import me.xjanua.spring.backend.service.ClientAppService;

@RequestMapping("/client-apps")
@RequiredArgsConstructor
@RestController
public class ClientAppController {

    private final ClientAppService clientAppService;
    private final ClientAppMapper clientAppMapper;

    @PreAuthorize("hasAuthority('CLIENT_APP_READ')")
    @GetMapping
    public ResponseEntity<PaginationDTO.Response> getAll(@Filter Specification<ClientApp> spec, Pageable pageable) {
        PaginationDTO.Response result = clientAppService.fetchAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('CLIENT_APP_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ClientAppResponse> getById(@PathVariable UUID id) {
        ClientApp clientApp = clientAppService.findById(id);
        ClientAppResponse response = clientAppMapper.toResponse(clientApp);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('CLIENT_APP_READ')")
    @GetMapping("/client-id/{clientId}")
    public ResponseEntity<ClientAppResponse> getByClientId(@PathVariable String clientId) {
        ClientApp clientApp = clientAppService.findByClientId(clientId);
        ClientAppResponse response = clientAppMapper.toResponse(clientApp);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('CLIENT_APP_CREATE')")
    @PostMapping
    public ResponseEntity<ClientAppResponse> create(@RequestBody ClientAppRequest request) {
        ClientApp created = clientAppService.create(request);
        ClientAppResponse response = clientAppMapper.toResponseWithSecret(created);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('CLIENT_APP_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<ClientAppResponse> update(@PathVariable UUID id, @RequestBody ClientAppUpdateRequest request) {
        ClientApp updated = clientAppService.update(id, request);
        ClientAppResponse response = clientAppMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('CLIENT_APP_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        clientAppService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('CLIENT_APP_UPDATE')")
    @PostMapping("/{id}/regenerate-secret")
    public ResponseEntity<ClientAppResponse> regenerateSecret(@PathVariable UUID id) {
        ClientApp updated = clientAppService.regenerateSecret(id);
        ClientAppResponse response = clientAppMapper.toResponseWithSecret(updated);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('CLIENT_APP_UPDATE')")
    @PostMapping("/{id}/scopes")
    public ResponseEntity<ClientAppResponse> addScopes(@PathVariable UUID id, @RequestBody List<String> scopes) {
        ClientApp updated = clientAppService.addScopes(id, scopes);
        ClientAppResponse response = clientAppMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('CLIENT_APP_UPDATE')")
    @DeleteMapping("/{id}/scopes/{scopeCode}")
    public ResponseEntity<ClientAppResponse> removeScope(@PathVariable UUID id, @PathVariable String scopeCode) {
        ClientApp updated = clientAppService.removeScope(id, scopeCode);
        ClientAppResponse response = clientAppMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('CLIENT_APP_UPDATE')")
    @PostMapping("/{id}/redirect-uris")
    public ResponseEntity<ClientAppResponse> addRedirectUri(@PathVariable UUID id, @RequestParam String uri) {
        ClientApp updated = clientAppService.addRedirectUri(id, uri);
        ClientAppResponse response = clientAppMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('CLIENT_APP_UPDATE')")
    @DeleteMapping("/{id}/redirect-uris")
    public ResponseEntity<ClientAppResponse> removeRedirectUri(@PathVariable UUID id, @RequestParam String uri) {
        ClientApp updated = clientAppService.removeRedirectUri(id, uri);
        ClientAppResponse response = clientAppMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }
}
