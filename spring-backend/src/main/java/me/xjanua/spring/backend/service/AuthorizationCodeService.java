package me.xjanua.spring.backend.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.model.AuthorizationCode;
import me.xjanua.spring.backend.model.ClientApp;
import me.xjanua.spring.backend.model.User;
import me.xjanua.spring.backend.repository.AuthorizationCodeRepository;

@Service
@RequiredArgsConstructor
public class AuthorizationCodeService {

    private static final long AUTHORIZATION_CODE_VALIDITY_MINUTES = 5;

    private final AuthorizationCodeRepository authorizationCodeRepository;

    public String createRedirectUrl(
            User user,
            ClientApp clientApp,
            String redirectUri,
            String scopes) {
        AuthorizationCode authorizationCode = AuthorizationCode.builder()
                .code(generateAuthorizationCode())
                .clientApp(clientApp)
                .user(user)
                .redirectUri(redirectUri)
                .scopes(scopes)
                .expiresAt(LocalDateTime.now().plusMinutes(AUTHORIZATION_CODE_VALIDITY_MINUTES))
                .isUsed(false)
                .build();

        authorizationCodeRepository.save(authorizationCode);

        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("code", authorizationCode.getCode())
                .build()
                .encode()
                .toUriString();
    }

    private String generateAuthorizationCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().replace("-", "");
        } while (authorizationCodeRepository.existsByCode(code));
        return code;
    }
}
