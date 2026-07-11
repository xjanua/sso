package me.xjanua.spring.backend.dto.clientApp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClientAppResponse {
    private UUID id;
    private String clientId;
    private String clientSecret;
    private String name;
    private String description;
    private String logoUrl;
    private String status;
    private List<String> scopes;
    private List<String> redirectUris;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
