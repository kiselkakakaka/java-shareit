package ru.practicum.shareit.request.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestOutDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemRequestMapper {

    public static ItemRequest toEntity(ItemRequestCreateDto dto, User requester) {
        ItemRequest r = new ItemRequest();
        r.setDescription(dto.getDescription());
        r.setRequester(requester);
        r.setCreated(LocalDateTime.now());
        return r;
    }

    public static ItemRequestOutDto toOutDto(ItemRequest request, List<Item> items) {
        if (items == null) {
            items = Collections.emptyList();
        }

        List<ItemShortDto> itemDtos = items.stream()
                .map(ItemRequestMapper::toItemShortDto)
                .collect(Collectors.toList());

        return ItemRequestOutDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(itemDtos)
                .build();
    }

    private static ItemShortDto toItemShortDto(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }
}