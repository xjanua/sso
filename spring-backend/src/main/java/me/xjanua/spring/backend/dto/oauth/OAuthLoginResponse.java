package me.xjanua.spring.backend.dto.oauth;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OAuthLoginResponse {
    private String status;
    private String redirectUrl;
    private String consentRequestCode;
    private ConsentClientResponse client;
    private List<ConsentScopeResponse> scopes;
}
