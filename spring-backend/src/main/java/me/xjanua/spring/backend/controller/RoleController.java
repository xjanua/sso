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
import me.xjanua.spring.backend.dto.role.RoleRequest;
import me.xjanua.spring.backend.dto.role.RoleResponse;
import me.xjanua.spring.backend.mapper.RoleMapper;
import me.xjanua.spring.backend.model.Role;
import me.xjanua.spring.backend.service.RoleService;

@RequestMapping("/roles")
@RequiredArgsConstructor
@RestController
public class RoleController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @PreAuthorize("hasAuthority('ROLE_READ')")
    @GetMapping
    public ResponseEntity<PaginationDTO.Response> getAll(@Filter Specification<Role> spec, Pageable pageable) {
        PaginationDTO.Response result = roleService.fetchAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('ROLE_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getById(@PathVariable UUID id) {
        Role role = roleService.findById(id);
        RoleResponse response = roleMapper.toResponse(role);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    @PostMapping
    public ResponseEntity<RoleResponse> create(@RequestBody RoleRequest request) {
        Role created = roleService.create(request);
        RoleResponse response = roleMapper.toResponse(created);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> update(@PathVariable UUID id, @RequestBody RoleRequest request) {
        Role updated = roleService.update(id, request);
        RoleResponse response = roleMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

