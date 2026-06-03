package com.finance.accountservice.security.service;

import com.finance.accountservice.security.dto.request.LoginRequest;
import com.finance.accountservice.security.dto.request.RegisterRequest;
import com.finance.accountservice.security.dto.response.LoginResponse;
import com.finance.accountservice.security.jwt.JwtService;
import com.finance.accountservice.security.user.entity.Role;
import com.finance.accountservice.security.user.entity.UserEntity;
import com.finance.accountservice.security.user.repository.UserRepository;
import com.finance.accountservice.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditService auditService;

    public void register(RegisterRequest request) {

        if(userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(
                    "Username already exists"
            );
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        auditService.log(
                "REGISTER",
                request.getUsername(),
                "User",
                null,
                "New user registered with email: " + request.getEmail(),
                "SUCCESS"
        );
    }

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        String token = jwtService.generateToken(
                request.getUsername()
        );

        auditService.log(
                "LOGIN",
                request.getUsername(),
                "User",
                null,
                "Successful login",
                "SUCCESS"
        );

        return new LoginResponse(token);
    }
}