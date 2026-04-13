package com.ansh.service;


import com.ansh.dto.request.AuthRequest;
import com.ansh.dto.response.ApiResponse.AuthResponse;
import com.ansh.entity.User;
import com.ansh.exception.ConflictException;
import com.ansh.repository.UserRepository;
import com.ansh.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(AuthRequest.Register req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ConflictException("email already in use");
        }
        User user = User.builder()
                .name(req.name())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .build();
        userRepository.save(user);
        log.info("Registered new user: {}", user.getEmail());
        return AuthResponse.of(jwtService.generateToken(user), user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest.Login req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new BadCredentialsException("invalid credentials"));
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new BadCredentialsException("invalid credentials");
        }
        log.info("User logged in: {}", user.getEmail());
        return AuthResponse.of(jwtService.generateToken(user), user);
    }
}