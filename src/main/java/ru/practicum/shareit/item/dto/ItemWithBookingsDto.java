package ru.practicum.shareit.item.dto;

import java.util.List;
import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ItemWithBookingsDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    private BookingShortDto lastBooking;  // только для владельца
    private BookingShortDto nextBooking;  // только для владельца

    private List<CommentDto> comments;
}
