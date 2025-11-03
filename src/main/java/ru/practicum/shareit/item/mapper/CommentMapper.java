package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class CommentMapper {

    private CommentMapper() {
    }

    public static ItemDto.CommentDto toDto(Comment c) {
        if (c == null) return null;
        ItemDto.CommentDto dto = new ItemDto.CommentDto();
        dto.setId(c.getId());
        dto.setText(c.getText());
        dto.setAuthorName(c.getAuthor() != null ? c.getAuthor().getName() : null);
        dto.setCreated(c.getCreated());
        return dto;
    }

    public static List<ItemDto.CommentDto> toDtoList(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return List.of();
        }
        List<ItemDto.CommentDto> list = new ArrayList<>(comments.size());
        for (Comment c : comments) {
            list.add(toDto(c));
        }
        return list;
    }

    public static Comment from(Long authorId, String authorName, Item item, String text, LocalDateTime created) {
        Comment c = new Comment();
        c.setText(text);
        c.setItem(item);

        User author = new User();
        author.setId(authorId);
        author.setName(authorName);
        c.setAuthor(author);

        c.setCreated(created);
        return c;
    }
}