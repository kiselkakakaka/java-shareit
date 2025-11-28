package ru.practicum.shareit.booking.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingState;

@Component
public class BookingStateConverter implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String source) {
        return BookingState.from(source).orElseThrow(
                () -> new IllegalArgumentException("Unknown state: " + source)
        );
    }
}
