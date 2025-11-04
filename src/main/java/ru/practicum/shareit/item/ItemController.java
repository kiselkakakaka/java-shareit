package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final ItemService itemService;

    public ItemController(final ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader(USER_HEADER) Long userId,
                                          @Valid @RequestBody ItemDto dto) {
        ItemDto created = itemService.create(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader(USER_HEADER) Long userId,
                                          @PathVariable Long itemId,
                                          @RequestBody ItemDto patch) {
        ItemDto updated = itemService.update(userId, itemId, patch);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> get(@RequestHeader(USER_HEADER) Long requesterId,
                                       @PathVariable Long itemId) {
        ItemDto dto = itemService.getById(requesterId, itemId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public List<ItemDto> ownerItems(@RequestHeader(USER_HEADER) Long ownerId) {
        return itemService.getOwnerItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        } else {
            return itemService.search(text);
        }
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<ItemDto.CommentDto> addComment(@RequestHeader(USER_HEADER) Long userId,
                                                         @PathVariable Long itemId,
                                                         @Valid @RequestBody CommentCreateDto body) {
        ItemDto.CommentDto dto = itemService.addComment(userId, itemId, body.getText());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}