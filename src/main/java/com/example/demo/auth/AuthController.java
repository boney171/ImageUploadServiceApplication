package com.example.demo.auth;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.LoginModel;
import com.example.demo.user.dto.UserRegistrationRequestModel;
import com.example.demo.user.dto.UserResponseModel;
import com.example.demo.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginModel loginRequest) {
        AuthResponse authenticationResponse = authService.login(loginRequest);
        return ResponseEntity.ok(authenticationResponse);
    }

    @PostMapping("/registration")
    public ResponseEntity<UserResponseModel> registerUser(@Valid @RequestBody UserRegistrationRequestModel user) throws ResponseStatusException {
        return ResponseEntity.status(201).body(userService.createUser(user));
    }
}
