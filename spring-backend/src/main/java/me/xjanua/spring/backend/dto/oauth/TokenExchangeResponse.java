package me.xjanua.spring.backend.dto.oauth;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenExchangeResponse {
    private UUID userId;
    private String email;
    private String name;
    private String avatar;
    private String phoneNumber;
    private List<String> scopes;
}
