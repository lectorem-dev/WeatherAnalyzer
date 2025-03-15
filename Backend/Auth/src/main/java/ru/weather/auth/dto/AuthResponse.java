package ru.weather.auth.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Setter
@Getter
public class AuthResponse {
    private UUID id;
    private String role;
    private String token;

    public AuthResponse(UUID id, String role, String token) {
        this.id = id;
        this.role = role;
        this.token = token;
    }
}
