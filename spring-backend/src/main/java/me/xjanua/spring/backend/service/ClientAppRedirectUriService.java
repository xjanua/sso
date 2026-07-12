package me.xjanua.spring.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.exception.BadRequestException;
import me.xjanua.spring.backend.model.ClientApp;
import me.xjanua.spring.backend.model.ClientAppRedirectUri;
import me.xjanua.spring.backend.repository.ClientAppRedirectUriRepository;

@Service
@RequiredArgsConstructor
public class ClientAppRedirectUriService {

    private final ClientAppRedirectUriRepository clientAppRedirectUriRepository;

    public List<ClientAppRedirectUri> findByClientAppId(UUID clientAppId) {
        return clientAppRedirectUriRepository.findByClientAppId(clientAppId);
    }

    public boolean exists(UUID clientAppId, String redirectUri) {
        return clientAppRedirectUriRepository.existsByClientAppIdAndRedirectUri(clientAppId, redirectUri);
    }

    @Transactional
    public ClientAppRedirectUri addRedirectUri(ClientApp clientApp, String redirectUri) {
        if (clientAppRedirectUriRepository.existsByClientAppIdAndRedirectUri(clientApp.getId(), redirectUri)) {
            throw new BadRequestException("Redirect URI already exists for this client app");
        }

        ClientAppRedirectUri appRedirectUri = ClientAppRedirectUri.builder()
                .clientApp(clientApp)
                .redirectUri(redirectUri)
                .build();

        return clientAppRedirectUriRepository.save(appRedirectUri);
    }

    @Transactional
    public void removeRedirectUri(ClientApp clientApp, String redirectUri) {
        clientAppRedirectUriRepository.findByClientAppId(clientApp.getId()).stream()
                .filter(uri -> uri.getRedirectUri().equals(redirectUri))
                .findFirst()
                .ifPresent(clientAppRedirectUriRepository::delete);
    }

    @Transactional
    public void deleteByClientAppId(UUID clientAppId) {
        clientAppRedirectUriRepository.deleteByClientAppId(clientAppId);
    }

    @Transactional
    public void replaceRedirectUris(ClientApp clientApp, List<String> redirectUris) {
        clientAppRedirectUriRepository.deleteByClientAppId(clientApp.getId());

        if (redirectUris != null && !redirectUris.isEmpty()) {
            for (String uri : redirectUris) {
                addRedirectUri(clientApp, uri);
            }
        }
    }
}
