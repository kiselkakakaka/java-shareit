package ru.practicum.shareit.client;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public abstract class BaseClient {
    private final RestTemplate restTemplate;
    private final String baseUrl;

    protected BaseClient(RestTemplate restTemplate, String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    protected ResponseEntity<Object> get(String path, Long userId) {
        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders(userId));
        return restTemplate.exchange(baseUrl + path, HttpMethod.GET, entity, Object.class);
    }

    protected ResponseEntity<Object> get(String path, Long userId, Map<String, Object> params) {
        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders(userId));
        return restTemplate.exchange(baseUrl + path, HttpMethod.GET, entity, Object.class, params);
    }

    protected ResponseEntity<Object> post(String path, Long userId, Object body) {
        HttpEntity<Object> entity = new HttpEntity<>(body, defaultHeaders(userId));
        return restTemplate.exchange(baseUrl + path, HttpMethod.POST, entity, Object.class);
    }

    protected ResponseEntity<Object> patch(String path, Long userId, Object body) {
        HttpEntity<Object> entity = new HttpEntity<>(body, defaultHeaders(userId));
        return restTemplate.exchange(baseUrl + path, HttpMethod.PATCH, entity, Object.class);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        HttpEntity<Void> entity = new HttpEntity<>(defaultHeaders(userId));
        return restTemplate.exchange(baseUrl + path, HttpMethod.DELETE, entity, Object.class);
    }

    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) headers.add("X-Sharer-User-Id", String.valueOf(userId));
        return headers;
    }
}