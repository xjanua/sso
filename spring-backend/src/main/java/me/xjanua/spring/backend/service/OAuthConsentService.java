package me.xjanua.spring.backend.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.oauth.ConsentRequest;
import me.xjanua.spring.backend.dto.oauth.ConsentResponse;
import me.xjanua.spring.backend.enums.AuthorizationRequestStatus;
import me.xjanua.spring.backend.exception.BadRequestException;
import me.xjanua.spring.backend.model.AuthorizationRequest;
import me.xjanua.spring.backend.repository.AuthorizationRequestRepository;

@Service
@RequiredArgsConstructor
public class OAuthConsentService {

    private final AuthorizationRequestRepository authorizationRequestRepository;
    private final UserConsentService userConsentService;
    private final AuthorizationCodeService authorizationCodeService;

    @Transactional
    public ConsentResponse decide(ConsentRequest request) {
        AuthorizationRequest authorizationRequest = authorizationRequestRepository
                .findByRequestCode(request.getConsentRequestCode())
                .orElseThrow(() -> new BadRequestException("Invalid consent request"));

        validateAuthorizationRequest(authorizationRequest);

        if (!request.getApproved()) {
            authorizationRequest.setStatus(AuthorizationRequestStatus.DENIED);
            authorizationRequestRepository.save(authorizationRequest);

            return ConsentResponse.builder()
                    .redirectUrl(buildDeniedRedirectUrl(authorizationRequest.getRedirectUri()))
                    .build();
        }

        userConsentService.grant(
                authorizationRequest.getUser(),
                authorizationRequest.getClientApp(),
                authorizationRequest.getScopes());

        String redirectUrl = authorizationCodeService.createRedirectUrl(
                authorizationRequest.getUser(),
                authorizationRequest.getClientApp(),
                authorizationRequest.getRedirectUri(),
                authorizationRequest.getScopes());

        authorizationRequest.setStatus(AuthorizationRequestStatus.APPROVED);
        authorizationRequestRepository.save(authorizationRequest);

        return ConsentResponse.builder()
                .redirectUrl(redirectUrl)
                .build();
    }

    private void validateAuthorizationRequest(AuthorizationRequest authorizationRequest) {
        if (authorizationRequest.getStatus() != AuthorizationRequestStatus.PENDING) {
            throw new BadRequestException("Consent request has already been processed");
        }

        if (authorizationRequest.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Consent request has expired");
        }
    }

    private String buildDeniedRedirectUrl(String redirectUri) {
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("error", "access_denied")
                .build()
                .encode()
                .toUriString();
    }
}
