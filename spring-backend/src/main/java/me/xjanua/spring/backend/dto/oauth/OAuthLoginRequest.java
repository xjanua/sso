package me.xjanua.spring.backend.dto.oauth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthLoginRequest {

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email address")
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;

    @NotEmpty(message = "Client ID is required")
    private String clientId;

    @NotEmpty(message = "Redirect URI is required")
    private String redirectUri;
}
