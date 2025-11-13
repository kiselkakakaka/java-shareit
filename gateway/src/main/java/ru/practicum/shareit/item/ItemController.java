package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemClient itemClient;
    private static final String USER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER) long ownerId,
                                         @RequestBody @Valid ItemDto dto) {
        return itemClient.create(ownerId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER) long ownerId,
                                         @PathVariable long itemId,
                                         @RequestBody Map<String, Object> patch) {
        return itemClient.update(ownerId, itemId, patch);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader(value = USER, required = false) Long requesterId,
                                      @PathVariable long itemId) {
        return itemClient.getById(requesterId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> ownerItems(@RequestHeader(USER) long ownerId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                             @RequestParam(defaultValue = "10") @Positive int size) {
        return itemClient.getOwnerItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(value = USER, required = false) Long requesterId,
                                         @RequestParam String text,
                                         @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10") @Positive int size) {
        return itemClient.search(requesterId == null ? 0 : requesterId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> comment(@RequestHeader(USER) long userId,
                                          @PathVariable long itemId,
                                          @RequestBody @Valid CommentDto dto) {
        return itemClient.addComment(userId, itemId, dto);
    }

    public record ItemDto(
            Long id,
            @NotBlank @Length(max = 255) String name,
            @NotBlank @Length(max = 2000) String description,
            @NotNull Boolean available,
            Long requestId
    ) {}

    public record CommentDto(
            Long id,
            @NotBlank @Length(max = 2000) String text
    ) {}
}