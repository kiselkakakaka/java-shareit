package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

public final class ItemMapper {

    private ItemMapper() { }

    public static ItemDto toItemDto(Item item) {
        if (item == null) return null;
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setAvailable(item.getAvailable());
        dto.setRequestId(item.getRequestId());
        dto.setComments(Collections.emptyList());
        return dto;
    }

    public static ItemDto toItemDto(Item item,
                                    ItemDto.BookingShort last,
                                    ItemDto.BookingShort next,
                                    List<ItemDto.CommentDto> comments) {
        ItemDto dto = toItemDto(item);
        dto.setLastBooking(last);
        dto.setNextBooking(next);
        dto.setComments(comments == null ? Collections.emptyList() : comments);
        return dto;
    }

    public static Item fromDto(ItemDto dto, Long ownerId, Long requestId) {
        if (dto == null) return null;

        Item item = new Item();
        item.setId(dto.getId());
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());

        if (ownerId != null) {
            User owner = new User();
            owner.setId(ownerId);
            item.setOwner(owner);
        }

        item.setRequestId(requestId != null ? requestId : dto.getRequestId());
        return item;
    }
}