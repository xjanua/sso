package me.xjanua.spring.backend.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import me.xjanua.spring.backend.dto.clientApp.ClientAppResponse;
import me.xjanua.spring.backend.model.ClientApp;
import me.xjanua.spring.backend.model.ClientAppRedirectUri;
import me.xjanua.spring.backend.model.ClientAppScope;

@Component
public class ClientAppMapper {

    public ClientAppResponse toResponse(ClientApp clientApp) {
        if (clientApp == null) {
            return null;
        }

        List<String> scopes = clientApp.getScopes() != null
                ? clientApp.getScopes().stream()
                        .map(ClientAppScope::getScope)
                        .map(scope -> scope.getCode())
                        .collect(Collectors.toList())
                : List.of();

        List<String> redirectUris = clientApp.getRedirectUris() != null
                ? clientApp.getRedirectUris().stream()
                        .map(ClientAppRedirectUri::getRedirectUri)
                        .collect(Collectors.toList())
                : List.of();

        return ClientAppResponse.builder()
                .id(clientApp.getId())
                .clientId(clientApp.getClientId())
                .name(clientApp.getName())
                .description(clientApp.getDescription())
                .logoUrl(clientApp.getLogoUrl())
                .status(clientApp.getStatus().name())
                .scopes(scopes)
                .redirectUris(redirectUris)
                .createdAt(clientApp.getCreatedAt())
                .updatedAt(clientApp.getUpdatedAt())
                .build();
    }

    public ClientAppResponse toResponseWithSecret(ClientApp clientApp) {
        ClientAppResponse response = toResponse(clientApp);
        if (response != null) {
            response.setClientSecret(clientApp.getClientSecret());
        }
        return response;
    }
}
