package me.xjanua.spring.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.oauth.AuthorizationResponse;
import me.xjanua.spring.backend.dto.oauth.ConsentRequest;
import me.xjanua.spring.backend.dto.oauth.ConsentResponse;
import me.xjanua.spring.backend.dto.oauth.OAuthLoginRequest;
import me.xjanua.spring.backend.dto.oauth.OAuthLoginResponse;
import me.xjanua.spring.backend.dto.oauth.TokenExchangeRequest;
import me.xjanua.spring.backend.dto.oauth.TokenExchangeResponse;
import me.xjanua.spring.backend.service.OAuthAuthorizationService;
import me.xjanua.spring.backend.service.OAuthConsentService;
import me.xjanua.spring.backend.service.OAuthLoginService;
import me.xjanua.spring.backend.service.OAuthTokenService;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthAuthorizationService oauthAuthorizationService;
    private final OAuthLoginService oauthLoginService;
    private final OAuthTokenService oauthTokenService;
    private final OAuthConsentService oauthConsentService;

    @GetMapping("/authorize")
    public ResponseEntity<AuthorizationResponse> authorize(
            @RequestParam("client_id") String clientId,
            @RequestParam("redirect_uri") String redirectUri) {
        AuthorizationResponse response = oauthAuthorizationService.validateAuthorizationRequest(clientId, redirectUri);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<OAuthLoginResponse> login(@Valid @RequestBody OAuthLoginRequest request) {
        return ResponseEntity.ok(oauthLoginService.login(request));
    }

    @PostMapping("/consent")
    public ResponseEntity<ConsentResponse> consent(@Valid @RequestBody ConsentRequest request) {
        return ResponseEntity.ok(oauthConsentService.decide(request));
    }

    @PostMapping("/token")
    public ResponseEntity<TokenExchangeResponse> exchangeCode(
            @Valid @RequestBody TokenExchangeRequest request) {
        return ResponseEntity.ok(oauthTokenService.exchangeCode(request));
    }
}
