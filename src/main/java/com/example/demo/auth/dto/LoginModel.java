package com.example.demo.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginModel(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password) {
}
