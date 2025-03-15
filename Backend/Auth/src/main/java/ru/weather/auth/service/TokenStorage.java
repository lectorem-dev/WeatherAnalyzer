package ru.weather.auth.service;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStorage {
    private final Map<UUID, String> tokenMap = new ConcurrentHashMap<>();

    public void storeToken(UUID userId, String token) {
        tokenMap.put(userId, token);
    }

    public boolean isTokenValid(UUID userId, String token) {
        return token.equals(tokenMap.get(userId));
    }

    public void revokeToken(UUID userId) {
        tokenMap.remove(userId);
    }
}