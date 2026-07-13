package me.xjanua.spring.backend.dto.oauth;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenExchangeRequest {

    @NotEmpty(message = "Authorization code is required")
    private String code;

    @NotEmpty(message = "Client ID is required")
    private String clientId;

    @NotEmpty(message = "Client secret is required")
    private String clientSecret;

    @NotEmpty(message = "Redirect URI is required")
    private String redirectUri;
}
