package ru.practicum.shareit.request.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private static final String USER = "X-Sharer-User-Id";
    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER) Long userId,
                                         @RequestBody @Valid ItemRequestCreateDto dto) {
        return client.create(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> own(@RequestHeader(USER) Long userId) {
        return client.own(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> all(@RequestHeader(USER) Long userId,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                      @RequestParam(defaultValue = "10") @Positive Integer size) {
        return client.all(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER) Long userId,
                                          @PathVariable Long requestId) {
        return client.getById(userId, requestId);
    }
}
