package me.xjanua.spring.backend.service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.UserDetailsCustom;
import me.xjanua.spring.backend.dto.auth.AuthenticationRequest;
import me.xjanua.spring.backend.dto.oauth.OAuthLoginRequest;
import me.xjanua.spring.backend.dto.oauth.OAuthLoginResponse;
import me.xjanua.spring.backend.model.AuthorizationCode;
import me.xjanua.spring.backend.model.ClientApp;
import me.xjanua.spring.backend.model.User;
import me.xjanua.spring.backend.repository.AuthorizationCodeRepository;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {

    private static final long AUTHORIZATION_CODE_VALIDITY_MINUTES = 5;

    private final AuthService authService;
    private final OAuthAuthorizationService oauthAuthorizationService;
    private final ClientAppScopeService clientAppScopeService;
    private final AuthorizationCodeRepository authorizationCodeRepository;

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

        AuthorizationCode authorizationCode = AuthorizationCode.builder()
                .code(generateAuthorizationCode())
                .clientApp(clientApp)
                .user(user)
                .redirectUri(request.getRedirectUri())
                .scopes(resolveScopes(clientApp))
                .expiresAt(LocalDateTime.now().plusMinutes(AUTHORIZATION_CODE_VALIDITY_MINUTES))
                .isUsed(false)
                .build();

        authorizationCodeRepository.save(authorizationCode);

        return OAuthLoginResponse.builder()
                .redirectUrl(buildRedirectUrl(request.getRedirectUri(), authorizationCode.getCode()))
                .build();
    }

    private String resolveScopes(ClientApp clientApp) {
        return clientAppScopeService.findByClientAppId(clientApp.getId()).stream()
                .map(clientAppScope -> clientAppScope.getScope().getCode())
                .sorted()
                .collect(Collectors.joining(","));
    }

    private String generateAuthorizationCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().replace("-", "");
        } while (authorizationCodeRepository.existsByCode(code));
        return code;
    }

    private String buildRedirectUrl(String redirectUri, String code) {
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("code", code)
                .build()
                .encode()
                .toUriString();
    }
}
