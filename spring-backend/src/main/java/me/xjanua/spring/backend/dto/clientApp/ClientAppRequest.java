package me.xjanua.spring.backend.dto.clientApp;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientAppRequest {
    private String name;
    private String description;
    private String logoUrl;
    private List<UUID> scopeIds;
    private List<String> redirectUris;
}
