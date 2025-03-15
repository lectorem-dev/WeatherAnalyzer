package ru.weather.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class ProxyController {
    private final WebClient webClient;

    @Autowired
    public ProxyController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8001")  // Указываем базовый URL
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // Устанавливаем лимит 10MB
                .build();
    }

    // Прокси для GET-запросов
    @GetMapping("/**")
    public ResponseEntity<?> proxyGet(HttpServletRequest request) {
        String uri = request.getRequestURI().replace(request.getContextPath(), "");

        return webClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(String.class)
                .block();  // Блокируем вызов, чтобы дождаться ответа от проксируемого сервиса
    }
}
