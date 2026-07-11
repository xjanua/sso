package me.xjanua.spring.backend.mapper;

import org.mapstruct.Mapper;

import me.xjanua.spring.backend.dto.scope.ScopeResponse;
import me.xjanua.spring.backend.model.Scope;

@Mapper(componentModel = "spring")
public interface ScopeMapper {

    ScopeResponse toResponse(Scope scope);
}
