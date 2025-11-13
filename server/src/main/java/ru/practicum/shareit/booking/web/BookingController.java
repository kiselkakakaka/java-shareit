package ru.practicum.shareit.booking.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody BookingShortDto dto
    ) {
        return ResponseEntity.ok(bookingService.create(userId, dto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<?> approve(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        return ResponseEntity.ok(bookingService.approve(ownerId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long bookingId
    ) {
        return ResponseEntity.ok(bookingService.getById(userId, bookingId));
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(bookingService.getAll(userId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<?> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ResponseEntity.ok(bookingService.getOwnerBookings(ownerId, state, from, size));
    }
}
