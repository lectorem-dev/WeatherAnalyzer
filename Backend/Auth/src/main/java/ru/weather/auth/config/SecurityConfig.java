package ru.weather.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
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
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/sw/**").permitAll()
                        .anyRequest().authenticated() // Все остальные запросы требуют аутентификацию
                )
                .csrf(AbstractHttpConfigurer::disable) // Отключаем CSRF для API
                .cors(Customizer.withDefaults()); // Используем глобальную конфигурацию CORS

        return http.build();
    }

    // Конфигурация CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin("http://localhost:3000"); // Указываем ваш фронтенд
        corsConfiguration.addAllowedHeader("*"); // Разрешаем все заголовки
        corsConfiguration.addAllowedMethod("*"); // Разрешаем все методы (GET, POST, PUT, DELETE и т.д.)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}
