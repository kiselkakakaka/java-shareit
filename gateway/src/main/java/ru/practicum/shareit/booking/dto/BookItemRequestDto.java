package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import ru.practicum.shareit.booking.validation.StartBeforeEnd;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@StartBeforeEnd
public class BookItemRequestDto {
    @NotNull private Long itemId;
    @NotNull @Future private LocalDateTime start;
    @NotNull @Future private LocalDateTime end;
}
