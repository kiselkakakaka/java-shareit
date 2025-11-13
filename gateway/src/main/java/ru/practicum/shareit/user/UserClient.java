package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

@Component
public class UserClient extends BaseClient {

    public UserClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplate restTemplate) {
        super(restTemplate, serverUrl + "/users");
    }

    public ResponseEntity<Object> create(Object dto) {
        return post("", null, dto);
    }

    public ResponseEntity<Object> update(Long userId, Object patch) {
        return patch("/" + userId, userId, patch);
    }

    public ResponseEntity<Object> getById(Long userId) {
        return get("/" + userId, null);
    }

    public ResponseEntity<Object> getAll() {
        return get("", null);
    }

    public ResponseEntity<Object> delete(Long userId) {
        return delete("/" + userId, null);
    }
}