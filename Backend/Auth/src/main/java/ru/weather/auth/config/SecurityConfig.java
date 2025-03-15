package ru.weather.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.weather.auth.service.JwtUtil;
import ru.weather.auth.service.TokenStorage;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final TokenStorage tokenStorage;

    public SecurityConfig(JwtUtil jwtUtil, TokenStorage tokenStorage) {
        this.jwtUtil = jwtUtil;
        this.tokenStorage = tokenStorage;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, tokenStorage), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/auth/login").permitAll() // Разрешаем доступ к /auth/login
                        .anyRequest().authenticated() // Все остальные запросы требуют аутентификацию
                )
                .csrf(AbstractHttpConfigurer::disable); // Отключаем CSRF для API

        return http.build();
    }
}
