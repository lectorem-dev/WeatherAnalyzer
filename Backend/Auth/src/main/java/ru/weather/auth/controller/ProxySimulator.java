package ru.weather.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ProxySimulator {
    private final WebClient webClient;

    @Autowired
    public ProxySimulator(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://simulator:8002")  // Указываем базовый URL
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // Устанавливаем лимит 10MB
                .build();
    }

    @GetMapping("/realtime-weather-simulator/**")
    public ResponseEntity<?> proxyGet(HttpServletRequest request) {
        String uri = request.getRequestURI().replace(request.getContextPath(), "");
        String queryString = request.getQueryString(); // Получаем строку запроса

        String fullUri = queryString != null ? uri + "?" + queryString : uri;

        return proxyRequest(fullUri);  // Блокируем вызов, чтобы дождаться ответа от проксируемого сервиса
    }

    private ResponseEntity<?> proxyRequest(String uri) {
        try {
            return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .toEntity(String.class)
                    .block();  // Блокируем вызов, чтобы дождаться ответа от проксируемого сервиса
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Ошибка проксирования", e);
        }
    }
}
