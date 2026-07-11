package me.xjanua.spring.backend.dto.scope;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScopeRequest {
    private String code;
    private String description;
    private Boolean isDefault;
}
