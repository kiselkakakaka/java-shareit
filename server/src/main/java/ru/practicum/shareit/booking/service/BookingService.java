package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.*;
import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingShortDto dto);
    BookingDto approve(Long ownerId, Long bookingId, boolean approved);
    BookingDto getById(Long userId, Long bookingId);
    List<BookingDto> getAll(Long userId, BookingState state, Integer from, Integer size);
    List<BookingDto> getOwnerBookings(Long ownerId, BookingState state, Integer from, Integer size);
}
