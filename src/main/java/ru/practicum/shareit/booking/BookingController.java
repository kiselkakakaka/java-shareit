package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    private final BookingService bookingService;

    @PostMapping(consumes = "application/json", produces = "application/json")
    public BookingDto createBooking(@RequestHeader(USER_HEADER) Long userId,
                                    @RequestBody BookingCreateDto dto) {
        return bookingService.create(userId, dto);
    }

    @PatchMapping(path = "/{bookingId}", produces = "application/json")
    public BookingDto approveBooking(@RequestHeader(USER_HEADER) Long ownerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping(path = "/{bookingId}", produces = "application/json")
    public BookingDto getBooking(@RequestHeader(USER_HEADER) Long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.get(userId, bookingId);
    }

    @GetMapping(produces = "application/json")
    public List<BookingDto> getUserBookings(@RequestHeader(USER_HEADER) Long userId,
                                            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getForBooker(userId, state);
    }

    @GetMapping(path = "/owner", produces = "application/json")
    public List<BookingDto> getOwnerBookings(@RequestHeader(USER_HEADER) Long ownerId,
                                             @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getForOwner(ownerId, state);
    }
}