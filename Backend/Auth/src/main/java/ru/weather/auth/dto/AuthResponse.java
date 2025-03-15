package ru.weather.auth.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Setter
@Getter
public class AuthResponse {
    private UUID id;
    private String token;

    public AuthResponse(UUID id, String token) {
        this.id = id;
        this.token = token;
    }
}
