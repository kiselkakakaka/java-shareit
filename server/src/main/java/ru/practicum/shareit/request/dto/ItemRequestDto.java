package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {

    private Long id;

    @NotBlank
    @Size(max = 1000)
    private String description;

    private LocalDateTime created;

    @Builder.Default
    private List<ItemDto> items = List.of();
}
