package me.xjanua.spring.backend.service;

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
import me.xjanua.spring.backend.dto.scope.ScopeRequest;
import me.xjanua.spring.backend.dto.scope.ScopeResponse;
import me.xjanua.spring.backend.exception.BadRequestException;
import me.xjanua.spring.backend.exception.NotFoundException;
import me.xjanua.spring.backend.mapper.ScopeMapper;
import me.xjanua.spring.backend.model.Scope;
import me.xjanua.spring.backend.repository.ScopeRepository;
import me.xjanua.spring.backend.util.PaginationUtil;

@Service
@RequiredArgsConstructor
public class ScopeService {

    private final ScopeRepository scopeRepository;
    private final ScopeMapper scopeMapper;

    public Scope findById(UUID id) {
        return scopeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Scope not found"));
    }

    public Scope findByCode(String code) {
        return scopeRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Scope not found: " + code));
    }

    public List<Scope> findByIds(List<UUID> ids) {
        return scopeRepository.findAllById(ids);
    }

    public List<Scope> findByCodes(List<String> codes) {
        return scopeRepository.findByCodeIn(codes);
    }

    public List<Scope> findAll() {
        return scopeRepository.findAll();
    }

    public Scope save(Scope scope) {
        return scopeRepository.save(scope);
    }

    public PaginationDTO.Response fetchAll(Specification<Scope> spec, Pageable pageable) {
        Page<Scope> scopes = scopeRepository.findAll(spec, pageable);

        PaginationDTO.Info info = PaginationUtil.buildInfo(scopes, pageable);

        List<ScopeResponse> responses = scopes.getContent().stream()
                .map(scopeMapper::toResponse)
                .collect(Collectors.toList());

        PaginationDTO.Response response = new PaginationDTO.Response();
        response.setInfo(info);
        response.setResponse(responses);

        return response;
    }

    @Transactional
    public Scope create(ScopeRequest request) {
        if (scopeRepository.findByCode(request.getCode()).isPresent()) {
            throw new BadRequestException("Scope with this code already exists");
        }

        Scope scope = Scope.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .isDefault(request.getIsDefault() != null ? request.getIsDefault() : false)
                .build();

        return save(scope);
    }

    @Transactional
    public Scope update(UUID id, ScopeRequest request) {
        Scope scope = findById(id);

        if (request.getCode() != null && !request.getCode().equals(scope.getCode())) {
            if (scopeRepository.findByCode(request.getCode()).isPresent()) {
                throw new BadRequestException("Scope with this code already exists");
            }
            scope.setCode(request.getCode());
        }

        if (request.getDescription() != null) {
            scope.setDescription(request.getDescription());
        }

        if (request.getIsDefault() != null) {
            scope.setIsDefault(request.getIsDefault());
        }

        return save(scope);
    }

    @Transactional
    public void delete(UUID id) {
        Scope scope = findById(id);
        scopeRepository.delete(scope);
    }
}
