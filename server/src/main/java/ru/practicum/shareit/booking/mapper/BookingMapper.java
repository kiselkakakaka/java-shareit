package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {

    public Booking toModel(BookingShortDto bookingShortDto, User booker, Item item) {
        if (bookingShortDto == null || booker == null || item == null) {
            return null;
        }

        Booking booking = new Booking();
        booking.setStart(bookingShortDto.getStart());
        booking.setEnd(bookingShortDto.getEnd());
        booking.setBooker(booker);
        booking.setItem(item);

        return booking;
    }

    public BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        BookingDto.BookerShortDto bookerShortDto = null;
        if (booking.getBooker() != null) {
            bookerShortDto = new BookingDto.BookerShortDto(booking.getBooker().getId());
        }

        BookingDto.ItemShortDto itemShortDto = null;
        if (booking.getItem() != null) {
            itemShortDto = new BookingDto.ItemShortDto(
                    booking.getItem().getId(),
                    booking.getItem().getName()
            );
        }

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(bookerShortDto)
                .item(itemShortDto)
                .build();
    }
}