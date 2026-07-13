package me.xjanua.spring.backend.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.oauth.TokenExchangeRequest;
import me.xjanua.spring.backend.dto.oauth.TokenExchangeResponse;
import me.xjanua.spring.backend.enums.ClientAppStatus;
import me.xjanua.spring.backend.exception.BadRequestException;
import me.xjanua.spring.backend.model.AuthorizationCode;
import me.xjanua.spring.backend.model.ClientApp;
import me.xjanua.spring.backend.model.User;
import me.xjanua.spring.backend.repository.AuthorizationCodeRepository;

@Service
@RequiredArgsConstructor
public class OAuthTokenService {

    private final AuthorizationCodeRepository authorizationCodeRepository;

    @Transactional
    public TokenExchangeResponse exchangeCode(TokenExchangeRequest request) {
        AuthorizationCode authorizationCode = authorizationCodeRepository.findByCode(request.getCode())
                .orElseThrow(() -> new BadRequestException("Invalid authorization code"));

        validateAuthorizationCode(authorizationCode, request);

        authorizationCode.setIsUsed(true);
        authorizationCode.setUsedAt(LocalDateTime.now());
        authorizationCodeRepository.save(authorizationCode);

        return buildResponse(authorizationCode);
    }

    private void validateAuthorizationCode(AuthorizationCode authorizationCode, TokenExchangeRequest request) {
        ClientApp clientApp = authorizationCode.getClientApp();

        if (clientApp.getStatus() != ClientAppStatus.ACTIVE) {
            throw new BadRequestException("Client app is inactive");
        }

        if (!clientApp.getClientId().equals(request.getClientId())
                || !clientApp.getClientSecret().equals(request.getClientSecret())) {
            throw new BadCredentialsException("Invalid client credentials");
        }

        if (!authorizationCode.getRedirectUri().equals(request.getRedirectUri())) {
            throw new BadRequestException("Redirect URI does not match authorization request");
        }

        if (Boolean.TRUE.equals(authorizationCode.getIsUsed())) {
            throw new BadRequestException("Authorization code has already been used");
        }

        if (authorizationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Authorization code has expired");
        }
    }

    private TokenExchangeResponse buildResponse(AuthorizationCode authorizationCode) {
        User user = authorizationCode.getUser();
        List<String> scopes = parseScopes(authorizationCode.getScopes());

        TokenExchangeResponse.TokenExchangeResponseBuilder response = TokenExchangeResponse.builder()
                .userId(user.getId())
                .scopes(scopes);

        if (hasScope(scopes, "email")) {
            response.email(user.getEmail());
        }
        if (hasScope(scopes, "profile")) {
            response.name(user.getFullName());
        }
        if (hasScope(scopes, "avatar")) {
            response.avatar(user.getAvatarUrl());
        }
        if (hasScope(scopes, "phone")) {
            response.phoneNumber(user.getPhoneNumber());
        }

        return response.build();
    }

    private List<String> parseScopes(String scopes) {
        if (scopes == null || scopes.isBlank()) {
            return List.of();
        }

        return Arrays.stream(scopes.split(","))
                .map(String::trim)
                .filter(scope -> !scope.isEmpty())
                .toList();
    }

    private boolean hasScope(List<String> scopes, String expectedScope) {
        return scopes.stream().anyMatch(scope -> scope.equalsIgnoreCase(expectedScope));
    }
}
