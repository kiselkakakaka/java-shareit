package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public final class BookingMapper {
    private BookingMapper() {}

    public static BookingDto toDto(Booking b) {
        if (b == null) return null;
        BookingDto.Booker booker = new BookingDto.Booker(
                b.getBooker() != null ? b.getBooker().getId() : null
        );
        BookingDto.ItemShort item = new BookingDto.ItemShort(
                b.getItem() != null ? b.getItem().getId() : null,
                b.getItem() != null ? b.getItem().getName() : null
        );
        return new BookingDto(
                b.getId(),
                b.getStart(),
                b.getEnd(),
                b.getStatus(),
                booker,
                item
        );
    }
}