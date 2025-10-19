package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto dto);
    ItemDto update(Long ownerId, Long itemId, ItemDto patch);
    ItemDto getById(Long requesterId, Long itemId);
    List<ItemDto> getOwnerItems(Long ownerId);
    List<ItemDto> search(String text);
}
