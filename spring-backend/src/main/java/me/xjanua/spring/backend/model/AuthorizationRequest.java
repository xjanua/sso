package me.xjanua.spring.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.xjanua.spring.backend.enums.AuthorizationRequestStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authorization_requests")
public class AuthorizationRequest extends BaseEntity {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "request_code", unique = true, nullable = false, length = 255)
    private String requestCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_app_id", nullable = false)
    private ClientApp clientApp;

    @Column(name = "redirect_uri", nullable = false)
    private String redirectUri;

    @Column(name = "scopes", nullable = false)
    private String scopes;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AuthorizationRequestStatus status;
}
