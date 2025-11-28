package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private BookerShort booker;
    private ItemShort item;

    @Data
    public static class BookerShort {
        private Long id;
    }

    @Data
    public static class ItemShort {
        private Long id;
        private String name;
    }
}
