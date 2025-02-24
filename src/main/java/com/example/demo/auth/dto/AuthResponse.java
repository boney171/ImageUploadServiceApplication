package com.example.demo.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("auth_token")
        String authToken) {
}
