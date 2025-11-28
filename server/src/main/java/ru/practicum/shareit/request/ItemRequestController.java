package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestService service;

    @PostMapping
    public ItemRequestOutDto create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                    @RequestBody @Valid ItemRequestCreateDto dto) {
        log.info("Create item request from user {}: {}", userId, dto.getDescription());
        return service.create(userId, dto);
    }

    @GetMapping
    public List<ItemRequestOutDto> getOwn(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        log.info("Get own item requests for user {}", userId);
        return service.getOwn(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestOutDto> getAll(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                          @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get ALL item requests for user {}, from={}, size={}", userId, from, size);
        return service.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestOutDto getById(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                     @PathVariable @Positive Long requestId) {
        log.info("Get item request {} for user {}", requestId, userId);
        return service.getById(userId, requestId);
    }
}