package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public Booking toModel(BookingShortDto dto, User booker, Item item) {
        Booking b = new Booking();
        b.setStart(dto.getStart());
        b.setEnd(dto.getEnd());
        b.setBooker(booker);
        b.setItem(item);
        return b;
    }

    public BookingDto toDto(Booking b) {
        return BookingDto.builder()
                .id(b.getId())
                .start(b.getStart())
                .end(b.getEnd())
                .status(b.getStatus())
                .booker(new BookingDto.BookerShortDto(b.getBooker().getId()))
                .item(new BookingDto.ItemShortDto(b.getItem().getId(), b.getItem().getName()))
                .build();
    }
}
