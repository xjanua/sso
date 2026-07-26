package me.xjanua.spring.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.UserDetailsCustom;
import me.xjanua.spring.backend.dto.auth.AuthenticationRequest;
import me.xjanua.spring.backend.dto.oauth.ConsentClientResponse;
import me.xjanua.spring.backend.dto.oauth.ConsentScopeResponse;
import me.xjanua.spring.backend.dto.oauth.OAuthLoginRequest;
import me.xjanua.spring.backend.dto.oauth.OAuthLoginResponse;
import me.xjanua.spring.backend.enums.OAuthLoginStatus;
import me.xjanua.spring.backend.model.AuthorizationRequest;
import me.xjanua.spring.backend.model.ClientApp;
import me.xjanua.spring.backend.model.ClientAppScope;
import me.xjanua.spring.backend.model.User;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private final AuthService authService;
    private final OAuthAuthorizationService oauthAuthorizationService;
    private final ClientAppScopeService clientAppScopeService;
    private final UserConsentService userConsentService;
    private final AuthorizationRequestService authorizationRequestService;
    private final AuthorizationCodeService authorizationCodeService;

    @Transactional
    public OAuthLoginResponse login(OAuthLoginRequest request) {
        ClientApp clientApp = oauthAuthorizationService.validateClientApp(
                request.getClientId(),
                request.getRedirectUri());

        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        UserDetailsCustom userDetails = authService.authenticate(authRequest);
        User user = userDetails.getUser();
        List<ClientAppScope> clientAppScopes = clientAppScopeService.findByClientAppId(clientApp.getId());
        String scopes = resolveScopes(clientAppScopes);

        if (userConsentService.hasGrantedScopes(user, clientApp, scopes)) {
            String redirectUrl = authorizationCodeService.createRedirectUrl(
                    user,
                    clientApp,
                    request.getRedirectUri(),
                    scopes);

            return OAuthLoginResponse.builder()
                    .status(OAuthLoginStatus.AUTHORIZED.name())
                    .redirectUrl(redirectUrl)
                    .build();
        }

        AuthorizationRequest authorizationRequest = authorizationRequestService.create(
                user,
                clientApp,
                request.getRedirectUri(),
                scopes);

        return OAuthLoginResponse.builder()
                .status(OAuthLoginStatus.CONSENT_REQUIRED.name())
                .consentRequestCode(authorizationRequest.getRequestCode())
                .client(ConsentClientResponse.builder()
                        .name(clientApp.getName())
                        .logoUrl(clientApp.getLogoUrl())
                        .build())
                .scopes(buildScopeResponses(clientAppScopes))
                .build();
    }

    private String resolveScopes(List<ClientAppScope> clientAppScopes) {
        return clientAppScopes.stream()
                .map(clientAppScope -> clientAppScope.getScope().getCode())
                .sorted()
                .collect(Collectors.joining(","));
    }

    private List<ConsentScopeResponse> buildScopeResponses(List<ClientAppScope> clientAppScopes) {
        return clientAppScopes.stream()
                .map(clientAppScope -> ConsentScopeResponse.builder()
                        .code(clientAppScope.getScope().getCode())
                        .description(clientAppScope.getScope().getDescription())
                        .build())
                .sorted((first, second) -> first.getCode().compareTo(second.getCode()))
                .toList();
    }
}
