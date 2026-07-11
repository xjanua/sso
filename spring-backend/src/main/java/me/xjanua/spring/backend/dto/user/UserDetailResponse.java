package me.xjanua.spring.backend.dto.user;

import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import me.xjanua.spring.backend.dto.role.RoleSummaryResponse;
import me.xjanua.spring.backend.enums.UserStatus;

@Getter
@Setter
public class UserDetailResponse {

    private UUID id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phoneNumber;
    private UserStatus status;
    private List<RoleSummaryResponse> roles;
}
