package ru.practicum.shareit.booking.service;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

public interface BookingService {
    BookingDto create(Long userId, BookingCreateDto dto);
    BookingDto approve(Long ownerId, Long bookingId, boolean approved);
    BookingDto get(Long userId, Long bookingId);
    List<BookingDto> getForBooker(Long userId, String state);
    List<BookingDto> getForOwner(Long ownerId, String state);
}
