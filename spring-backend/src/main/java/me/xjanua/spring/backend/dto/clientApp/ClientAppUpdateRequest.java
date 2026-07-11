package me.xjanua.spring.backend.dto.clientApp;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientAppUpdateRequest {
    private String name;
    private String description;
    private String logoUrl;
    private String status;
    private List<String> scopes;
    private List<String> redirectUris;
}
