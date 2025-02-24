package com.example.demo.user.dto;

import com.example.demo.user.User;
import jakarta.validation.constraints.NotBlank;

public record UserRegistrationRequestModel(
        @NotBlank(message = "username can not be empty")
        String username,

        @NotBlank(message = "password can not be empty")
        String password,

        String name
) {
    public User toUserEntity() {
        return new User(null, username, password, name);
    }
};