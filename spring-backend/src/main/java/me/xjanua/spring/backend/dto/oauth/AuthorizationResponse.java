package me.xjanua.spring.backend.dto.oauth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthorizationResponse {
    private String clientId;
    private String clientName;
    private String logoUrl;
}
