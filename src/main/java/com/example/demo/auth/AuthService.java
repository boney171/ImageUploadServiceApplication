package com.example.demo.auth;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.LoginModel;
import com.example.demo.auth.security.JWTUtil;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JWTUtil jwt;
    private final PasswordEncoder passwordEncoder;
    Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthService(UserRepository userRepository, JWTUtil jwt, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwt = jwt;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(LoginModel loginRequest) {
        User existingUser = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> {
                    logger.warn("Login failed: User '{}' not found", loginRequest.username());
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist");
                });

        if (!passwordEncoder.matches(loginRequest.password(), existingUser.getPassword())) {
            logger.warn("Login failed: Incorrect password for user '{}'", existingUser.getUsername());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
        }

        logger.info("User '{}' logged in successfully", existingUser.getUsername());
        return new AuthResponse(jwt.generateToken(existingUser.getUsername(), existingUser.getId()));
    }
}
