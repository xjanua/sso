package me.xjanua.spring.backend.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.user.UserDetailResponse;
import me.xjanua.spring.backend.mapper.UserMapper;
import me.xjanua.spring.backend.model.User;
import me.xjanua.spring.backend.service.UserService;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PreAuthorize("hasAuthority('USER_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailResponse> getUser(@PathVariable UUID id) {
        User user = userService.findById(id);
        UserDetailResponse userDetailResponse = userMapper.toDetailResponse(user);
        return ResponseEntity.ok(userDetailResponse);
    }
}