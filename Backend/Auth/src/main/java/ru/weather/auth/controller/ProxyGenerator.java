package ru.weather.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ProxyGenerator {
    private final WebClient webClient;

    @Autowired
    public ProxyGenerator(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://generator:8001")  // Указываем базовый URL
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // Устанавливаем лимит 10MB
                .build();
    }

    @GetMapping("/charts/**")
    public ResponseEntity<?> proxyGet(HttpServletRequest request) {
        String uri = request.getRequestURI().replace(request.getContextPath(), "");
        return proxyRequest(uri);
    }

    @GetMapping("/generator/swagger-ui/**")
    public ResponseEntity<?> proxySwagger(HttpServletRequest request) {
        String uri = request.getRequestURI().replace(request.getContextPath(), "");
        return proxyRequest(uri);
    }

    @GetMapping("/generator/doc/**")
    public ResponseEntity<?> proxyApiDocs(HttpServletRequest request) {
        String uri = request.getRequestURI().replace(request.getContextPath(), "");
        return proxyRequest(uri);
    }

    @GetMapping("/generator/swagger-ui.html")
    public ResponseEntity<?> proxySwaggerHtml(HttpServletRequest request) {
        String uri = request.getRequestURI().replace(request.getContextPath(), "");
        return proxyRequest(uri);
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
