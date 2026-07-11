package me.xjanua.spring.backend.dto.permission;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionResponse {
    private UUID id;
    private String code;
    private String description;
}

