package ru.weather.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.weather.auth.dto.AuthResponse;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JdbcTemplate jdbcTemplate;
    private final JwtUtil jwtUtil;
    private final TokenStorage tokenStorage;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String AUTH_QUERY = """
        SELECT u.id, u.password
        FROM users u
        WHERE u.login = ?
    """;

    public AuthResponse authorize(String login, String password) {
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(AUTH_QUERY, login);
            String storedPasswordHash = (String) result.get("password");

            if (passwordEncoder.matches(password, storedPasswordHash)) {
                UUID userId = (UUID) result.get("id");

                String token = jwtUtil.generateToken(userId);
                tokenStorage.storeToken(userId, token);

                return new AuthResponse(userId, token);
            }
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Invalid login credentials");
        }

        return null;
    }

    public void logout(UUID userId) {
        tokenStorage.revokeToken(userId);
    }
}
