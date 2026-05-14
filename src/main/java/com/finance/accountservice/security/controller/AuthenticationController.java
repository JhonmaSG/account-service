package com.finance.accountservice.security.controller;

import com.finance.accountservice.security.dto.request.LoginRequest;
import com.finance.accountservice.security.dto.response.LoginResponse;
import com.finance.accountservice.security.dto.request.RegisterRequest;
import com.finance.accountservice.security.jwt.JwtService;
import com.finance.accountservice.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String token =
                jwtService.generateToken(
                        request.getUsername()
                );

        return ResponseEntity.ok(
                new LoginResponse(token)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody RegisterRequest request
    ) {

        authService.register(request);

        return ResponseEntity.ok("User registered");
    }
}