package me.xjanua.spring.backend.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.model.ClientApp;
import me.xjanua.spring.backend.model.User;
import me.xjanua.spring.backend.model.UserConsent;
import me.xjanua.spring.backend.repository.UserConsentRepository;

@Service
@RequiredArgsConstructor
public class UserConsentService {

    private final UserConsentRepository userConsentRepository;

    public boolean hasGrantedScopes(User user, ClientApp clientApp, String requestedScopes) {
        return userConsentRepository.findByUserIdAndClientAppId(user.getId(), clientApp.getId())
                .filter(consent -> consent.getRevokedAt() == null)
                .map(consent -> parseScopes(consent.getScopes()).containsAll(parseScopes(requestedScopes)))
                .orElse(false);
    }

    public void grant(User user, ClientApp clientApp, String scopes) {
        UserConsent consent = userConsentRepository.findByUserIdAndClientAppId(user.getId(), clientApp.getId())
                .orElseGet(() -> UserConsent.builder()
                        .user(user)
                        .clientApp(clientApp)
                        .build());

        consent.setScopes(scopes);
        consent.setGrantedAt(LocalDateTime.now());
        consent.setRevokedAt(null);
        userConsentRepository.save(consent);
    }

    private Set<String> parseScopes(String scopes) {
        if (scopes == null || scopes.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(scopes.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(scope -> !scope.isEmpty())
                .collect(Collectors.toSet());
    }
}
