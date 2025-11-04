package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import ru.practicum.shareit.item.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByItem_IdOrderByCreatedDesc(Long itemId);
}
