package ru.practicum.shareit.item;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

@RestController
@RequestMapping("/items")
public class ItemController {

    public static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader(USER_HEADER) Long userId,
                                          @RequestBody /*@Valid*/ ItemDto dto) {
        ItemDto created = service.create(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) {
        if (text == null || text.isBlank()) return List.of(); // важно для тестов
        return service.search(text);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long userId,
                          @PathVariable("itemId") Long itemId,
                          @RequestBody ItemDto patch) {
        return service.update(userId, itemId, patch);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(USER_HEADER) Long userId,
                           @PathVariable Long itemId) {
        return service.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader(USER_HEADER) Long userId) {
        return service.getOwnerItems(userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<ItemDto.CommentDto> addComment(@RequestHeader(USER_HEADER) Long userId,
                                                         @PathVariable Long itemId,
                                                         @RequestBody Map<String, String> body) {
        String text = body != null ? body.get("text") : null;
        ItemDto.CommentDto created = service.addComment(userId, itemId, text);
        return ResponseEntity.ok(created);
    }
}