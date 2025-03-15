package ru.weather.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.weather.auth.service.JwtUtil;
import ru.weather.auth.service.TokenStorage;

import java.io.IOException;
import java.util.UUID;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final TokenStorage tokenStorage;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtUtil jwtUtil, TokenStorage tokenStorage) {
        this.jwtUtil = jwtUtil;
        this.tokenStorage = tokenStorage;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractToken(request);

        if (token == null) {
            logger.warn("No token found in the request");
        } else if (jwtUtil.isTokenExpired(token)) {
            logger.warn("Token is expired");
        } else {
            UUID userId = jwtUtil.extractUserId(token); // Извлекаем userId из токена
            if (tokenStorage.isTokenValid(userId, token)) {
                // Если токен валиден, добавляем аутентификацию в SecurityContext
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId, null, null); // Устанавливаем роль, если нужно, или оставляем null
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.warn("Token is invalid for user: {}", userId);
            }
        }

        // Переходим к следующему фильтру
        filterChain.doFilter(request, response);
    }

    // Метод для извлечения токена из заголовка Authorization
    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // Убираем префикс "Bearer "
        }
        return null;
    }
}
