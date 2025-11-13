package ru.practicum.shareit.booking.dto;

import java.util.Arrays;
import java.util.Optional;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static Optional<BookingState> from(String s) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(s))
                .findFirst();
    }
}
