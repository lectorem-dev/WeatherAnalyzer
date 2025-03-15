package ru.weather.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.weather.auth.dto.AuthRequest;
import ru.weather.auth.dto.AuthResponse;
import ru.weather.auth.service.AuthService;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        String login = authRequest.getLogin();
        String password = authRequest.getPassword();
        AuthResponse authResponse = authService.authorize(login, password);

        if (authResponse == null) {
            return ResponseEntity.badRequest().body("Invalid login credentials");
        }

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestParam UUID userId) {
        authService.logout(userId);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
