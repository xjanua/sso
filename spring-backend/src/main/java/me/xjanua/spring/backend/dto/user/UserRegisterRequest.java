package me.xjanua.spring.backend.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}