package me.xjanua.spring.backend.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.PaginationDTO;
import me.xjanua.spring.backend.dto.scope.ScopeRequest;
import me.xjanua.spring.backend.dto.scope.ScopeResponse;
import me.xjanua.spring.backend.mapper.ScopeMapper;
import me.xjanua.spring.backend.model.Scope;
import me.xjanua.spring.backend.service.ScopeService;

@RequestMapping("/scopes")
@RequiredArgsConstructor
@RestController
public class ScopeController {

    private final ScopeService scopeService;
    private final ScopeMapper scopeMapper;

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping
    public ResponseEntity<PaginationDTO.Response> getAll(@Filter Specification<Scope> spec, Pageable pageable) {
        PaginationDTO.Response result = scopeService.fetchAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('SCOPE_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ScopeResponse> getById(@PathVariable UUID id) {
        Scope scope = scopeService.findById(id);
        ScopeResponse response = scopeMapper.toResponse(scope);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_CREATE')")
    @PostMapping
    public ResponseEntity<ScopeResponse> create(@RequestBody ScopeRequest request) {
        Scope created = scopeService.create(request);
        ScopeResponse response = scopeMapper.toResponse(created);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<ScopeResponse> update(@PathVariable UUID id, @RequestBody ScopeRequest request) {
        Scope updated = scopeService.update(id, request);
        ScopeResponse response = scopeMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('SCOPE_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        scopeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
