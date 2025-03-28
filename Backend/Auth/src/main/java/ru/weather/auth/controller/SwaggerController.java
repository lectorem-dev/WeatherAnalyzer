package ru.weather.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/sw")
public class SwaggerController {

    private static final String GENERATOR_URL = "http://generator:8001";
    private static final String SIMULATOR_URL = "http://simulator:8002";

    private final WebClient webClient;

    @Autowired
    public SwaggerController(WebClient.Builder webClientBuilder) {
        // Устанавливаем максимальный размер буфера и инициализируем WebClient
        this.webClient = webClientBuilder
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // Устанавливаем лимит 10MB
                .build();
    }

    @GetMapping("/{type}/v3/api-docs/**")
    public ResponseEntity<?> proxyApiDocs(@PathVariable String type, HttpServletRequest request) {
        return proxyRequest(request, type);
    }

    @GetMapping("/{type}/swagger-config")
    public ResponseEntity<?> proxySwaggerConfig(@PathVariable String type, HttpServletRequest request) {
        return proxyRequest(request, type);
    }

    @GetMapping("/{type}/swagger-ui.html")
    public ResponseEntity<?> proxySwaggerHtml(@PathVariable String type, HttpServletRequest request) {
        return proxyRequest(request, type);
    }

    @GetMapping("/{type}/swagger-ui/**")
    public ResponseEntity<?> proxySwaggerUi(@PathVariable String type, HttpServletRequest request) {
        return proxyRequest(request, type);
    }

    private ResponseEntity<?> proxyRequest(HttpServletRequest request, String type) {
        String uri = request.getRequestURI().replace(request.getContextPath(), "");
        String baseUrl = determineBaseUrl(type);

        if (baseUrl == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid proxy type");
        }

        // Прокси запрос через WebClient
        return webClient.get()
                .uri(baseUrl + uri)
                .retrieve()
                .toEntity(String.class)
                .map(response -> {
                    String contentType = response.getHeaders().getFirst("Content-Type");
                    if (contentType != null && contentType.contains("text/plain")) {
                        // Исправьте на правильный MIME тип
                        response.getHeaders().set("Content-Type", "application/javascript");
                    }
                    return ResponseEntity.status(response.getStatusCode())
                            .headers(response.getHeaders())
                            .body(response.getBody());
                })
                .block();
    }

    private String determineBaseUrl(String type) {
        switch (type) {
            case "generator":
                return GENERATOR_URL;
            case "simulator":
                return SIMULATOR_URL;
            default:
                return null;
        }
    }
}
