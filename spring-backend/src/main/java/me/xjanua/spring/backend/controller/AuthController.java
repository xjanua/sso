package me.xjanua.spring.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.UserDetailsCustom;
import me.xjanua.spring.backend.dto.auth.LoginDto;
import me.xjanua.spring.backend.dto.auth.ResponseLoginDto;
import me.xjanua.spring.backend.service.TokenService;

@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<ResponseLoginDto> login(@RequestBody LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        UserDetailsCustom userDetails = (UserDetailsCustom) authentication.getPrincipal();

        ResponseLoginDto res = new ResponseLoginDto();
        res.setAccessToken(tokenService.createAccessToken(userDetails.getId(), userDetails.getAuthorities()));
        return ResponseEntity.ok(res);
    }
}
