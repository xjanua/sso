package me.xjanua.spring.backend.dto.role;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleResponse {
    private UUID id;
    private String name;
}

