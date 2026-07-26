package me.xjanua.spring.backend.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.enums.AuthorizationRequestStatus;
import me.xjanua.spring.backend.model.AuthorizationRequest;
import me.xjanua.spring.backend.model.ClientApp;
import me.xjanua.spring.backend.model.User;
import me.xjanua.spring.backend.repository.AuthorizationRequestRepository;

@Service
@RequiredArgsConstructor
public class AuthorizationRequestService {

    private static final long AUTHORIZATION_REQUEST_VALIDITY_MINUTES = 5;

    private final AuthorizationRequestRepository authorizationRequestRepository;

    public AuthorizationRequest create(
            User user,
            ClientApp clientApp,
            String redirectUri,
            String scopes) {
        AuthorizationRequest request = AuthorizationRequest.builder()
                .requestCode(generateRequestCode())
                .user(user)
                .clientApp(clientApp)
                .redirectUri(redirectUri)
                .scopes(scopes)
                .expiresAt(LocalDateTime.now().plusMinutes(AUTHORIZATION_REQUEST_VALIDITY_MINUTES))
                .status(AuthorizationRequestStatus.PENDING)
                .build();

        return authorizationRequestRepository.save(request);
    }

    private String generateRequestCode() {
        String requestCode;
        do {
            requestCode = UUID.randomUUID().toString().replace("-", "");
        } while (authorizationRequestRepository.existsByRequestCode(requestCode));
        return requestCode;
    }
}
