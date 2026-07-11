package me.xjanua.spring.backend.dto.scope;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScopeResponse {
    private UUID id;
    private String code;
    private String description;
    private Boolean isDefault;
}
