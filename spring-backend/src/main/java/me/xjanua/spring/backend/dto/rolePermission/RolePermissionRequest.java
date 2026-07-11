package me.xjanua.spring.backend.dto.rolePermission;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePermissionRequest {
    private UUID roleId;
    private UUID permissionId;
}
