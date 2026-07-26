package me.xjanua.spring.backend.dto.oauth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsentRequest {

    @NotEmpty(message = "Consent request code is required")
    private String consentRequestCode;

    @NotNull(message = "Consent decision is required")
    private Boolean approved;
}
