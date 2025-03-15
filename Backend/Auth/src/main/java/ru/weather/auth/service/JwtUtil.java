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
            Base64.getDecoder().decode("sTcjr6irg3/xAGLWeMgjUW4IMUJSQ2dS8kEXE/a+76E=")
    );

    private final long expirationMs = 3600000; // 1 час

    public String generateToken(UUID userId, String role) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role)
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
