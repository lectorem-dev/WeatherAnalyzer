package ru.weather.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
public class ProxySimulator {
    private final WebClient webClient;

    @Autowired
    public ProxySimulator(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://simulator:8002")  // Указываем базовый URL
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // Устанавливаем лимит 1MB
                .build();
    }

    // Прокси для GET-запросов
    @GetMapping("/realtime-weather-simulator/**")
    public ResponseEntity<?> proxyGet(HttpServletRequest request) {
        String uri = request.getRequestURI().replace(request.getContextPath(), "");

        return webClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(String.class)
                .block();  // Блокируем вызов, чтобы дождаться ответа от проксируемого сервиса
    }
}
