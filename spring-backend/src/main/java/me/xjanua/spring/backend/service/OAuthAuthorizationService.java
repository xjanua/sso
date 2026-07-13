package me.xjanua.spring.backend.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.oauth.AuthorizationResponse;
import me.xjanua.spring.backend.enums.ClientAppStatus;
import me.xjanua.spring.backend.exception.BadRequestException;
import me.xjanua.spring.backend.model.ClientApp;

@Service
@RequiredArgsConstructor
public class OAuthAuthorizationService {

    private final ClientAppService clientAppService;
    private final ClientAppRedirectUriService clientAppRedirectUriService;

    public AuthorizationResponse validateAuthorizationRequest(String clientId, String redirectUri) {
        ClientApp clientApp = validateClientApp(clientId, redirectUri);

        return AuthorizationResponse.builder()
                .clientId(clientApp.getClientId())
                .clientName(clientApp.getName())
                .logoUrl(clientApp.getLogoUrl())
                .build();
    }

    public ClientApp validateClientApp(String clientId, String redirectUri) {
        ClientApp clientApp = clientAppService.findByClientId(clientId);

        if (clientApp.getStatus() != ClientAppStatus.ACTIVE) {
            throw new BadRequestException("Client app is inactive");
        }

        if (!clientAppRedirectUriService.exists(clientApp.getId(), redirectUri)) {
            throw new BadRequestException("Redirect URI is not registered for this client app");
        }

        return clientApp;
    }
}
