package ru.practicum.shareit.item.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ItemDto {
    private Long id;

    @NotBlank(message = "name must not be blank")
    private String name;

    private String description;

    @NotNull(message = "available must not be null")
    private Boolean available;

    private Long requestId;

    private BookingShort lastBooking;
    private BookingShort nextBooking;

    private List<CommentDto> comments;

    @Data
    public static class BookingShort {
        private Long id;
        private Long bookerId;
    }

    @Data
    public static class CommentDto {
        private Long id;
        private String text;
        private String authorName;
        private LocalDateTime created;
    }
}