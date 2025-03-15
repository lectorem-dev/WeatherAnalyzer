package ru.weather.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            Base64.getDecoder().decode("V5MzqC0OJiw57s8nAZazfCoOxBb63pYgwV1sXoYKCRo=") // HmacSHA256 - 256 бит
    );

    private final long expirationMs = 3600000; // 1 час

    public String generateToken(UUID userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Проверка, истек ли токен
    public boolean isTokenExpired(String token) {
        try {
            Date expirationDate = extractExpirationDate(token);
            return expirationDate.before(new Date());
        } catch (JwtException e) {
            return true;  // Если возникла ошибка при извлечении информации из токена, считаем его истекшим
        }
    }

    // Извлечение даты истечения токена
    private Date extractExpirationDate(String token) {
        return extractClaims(token).getExpiration();
    }

    // Извлечение субъекта из токена (UUID пользователя)
    public UUID extractUserId(String token) {
        return UUID.fromString(extractClaims(token).getSubject());
    }

    // Извлечение данных из токена (например, субъекта, даты и т.д.)
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
