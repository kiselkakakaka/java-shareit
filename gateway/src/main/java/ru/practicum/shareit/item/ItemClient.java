package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Component
public class ItemClient extends BaseClient {

    public ItemClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplate restTemplate) {
        super(restTemplate, serverUrl + "/items");
    }

    public ResponseEntity<Object> create(Long userId, Object dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, Object dto) {
        return patch("/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> getById(Long requesterId, Long itemId) {
        return get("/" + itemId, requesterId);
    }

    public ResponseEntity<Object> getOwnerItems(Long ownerId, Integer from, Integer size) {
        return get("?from={from}&size={size}", ownerId, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> search(long requesterId, String text, Integer from, Integer size) {
        return get("/search?text={text}&from={from}&size={size}", requesterId, Map.of("text", text, "from", from, "size", size));
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, Object dto) {
        return post("/" + itemId + "/comment", userId, dto);
    }
}