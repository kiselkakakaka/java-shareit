package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Component
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl,
                             RestTemplate restTemplate) {
        super(restTemplate, serverUrl + API_PREFIX);
    }

    public ResponseEntity<Object> create(Long userId, Object body) {
        return post("", userId, body);
    }

    public ResponseEntity<Object> own(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> all(Long userId, Integer from, Integer size) {
        return get("/all?from={from}&size={size}", userId, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> getById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }
}