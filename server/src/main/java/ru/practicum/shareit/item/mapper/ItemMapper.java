package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public final class ItemMapper {

    private ItemMapper() {
    }

    public static Item toModel(ItemDto dto, User owner, ItemRequest request) {
        if (dto == null) return null;

        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(owner)
                .request(request)
                .build();
    }

    public static ItemDto toDto(Item i, Booking last, Booking next, List<Comment> comments) {
        if (i == null) return null;

        ItemDto dto = ItemDto.builder()
                .id(i.getId())
                .name(i.getName())
                .description(i.getDescription())
                .available(i.getAvailable())
                .build();

        if (i.getRequest() != null) {
            dto.setRequestId(i.getRequest().getId());
        }

        if (last != null) {
            dto.setLastBooking(
                    new ItemDto.BookingShort(
                            last.getId(),
                            last.getBooker().getId()
                    )
            );
        }

        if (next != null) {
            dto.setNextBooking(
                    new ItemDto.BookingShort(
                            next.getId(),
                            next.getBooker().getId()
                    )
            );
        }

        if (comments != null) {
            dto.setComments(
                    comments.stream()
                            .map(ItemMapper::toCommentDto)
                            .toList()
            );
        }

        return dto;
    }

    public static CommentDto toCommentDto(Comment c) {
        if (c == null) return null;

        return CommentDto.builder()
                .id(c.getId())
                .text(c.getText())
                .authorName(c.getAuthor().getName())
                .created(c.getCreated())
                .build();
    }
}
