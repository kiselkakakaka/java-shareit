package ru.practicum.shareit.item.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    public ItemServiceImpl(final ItemRepository itemRepository,
                           final UserRepository userRepository,
                           final BookingRepository bookingRepository,
                           final CommentRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto create(final Long ownerId, final ItemDto dto) {
        requireUserExists(ownerId);

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BadRequestException("name обязателен");
        }
        if (dto.getDescription() == null) {
            throw new BadRequestException("description обязателен");
        }
        if (dto.getAvailable() == null) {
            throw new BadRequestException("available обязателен");
        }

        Item item = ItemMapper.fromDto(dto, ownerId, null);
        Item saved = itemRepository.save(item);
        return toDtoWithComments(saved);
    }

    @Override
    public ItemDto update(final Long ownerId, final Long itemId, final ItemDto patch) {
        requireUserExists(ownerId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: id=" + itemId));

        if (!ownerId.equals(item.getOwner().getId())) {
            throw new ForbiddenException("Редактировать вещь может только владелец");
        }

        if (patch.getName() != null) {
            item.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            item.setDescription(patch.getDescription());
        }
        if (patch.getAvailable() != null) {
            item.setAvailable(patch.getAvailable());
        }

        Item saved = itemRepository.save(item);
        return toDtoWithComments(saved);
    }

    @Override
    public ItemDto getById(final Long requesterId, final Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: id=" + itemId));
        return toDtoWithComments(item);
    }

    @Override
    public List<ItemDto> getOwnerItems(final Long ownerId) {
        requireUserExists(ownerId);
        return itemRepository.findByOwner(ownerId).stream()
                .map(this::toDtoWithComments)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(final String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto.CommentDto addComment(Long userId, Long itemId, String text) {
        if (text == null || text.isBlank()) {
            throw new BadRequestException("text обязателен");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: id=" + itemId));

        boolean hasCompletedBooking = bookingRepository.existsByBooker_IdAndItem_IdAndEndBeforeAndStatus(
                userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED
        );
        if (!hasCompletedBooking) {
            throw new BadRequestException("Оставлять комментарии могут только пользователи с завершённым бронированием этой вещи");
        }

        ru.practicum.shareit.item.model.Comment newComment = ru.practicum.shareit.item.model.Comment.builder()
                .text(text)
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        var saved = commentRepository.save(newComment);

        ItemDto.CommentDto dto = new ItemDto.CommentDto();
        dto.setId(saved.getId());
        dto.setText(saved.getText());
        dto.setAuthorName(saved.getAuthor().getName());
        dto.setCreated(saved.getCreated());
        return dto;
    }

    private void requireUserExists(final Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден: id=" + userId);
        }
    }

    private ItemDto toDtoWithComments(Item item) {
        ItemDto dto = ItemMapper.toItemDto(item);

        List<ItemDto.CommentDto> comments = commentRepository.findByItem_IdOrderByCreatedDesc(item.getId())
                .stream()
                .map(c -> {
                    ItemDto.CommentDto cd = new ItemDto.CommentDto();
                    cd.setId(c.getId());
                    cd.setText(c.getText());
                    cd.setAuthorName(c.getAuthor().getName());
                    cd.setCreated(c.getCreated());
                    return cd;
                })
                .collect(Collectors.toList());

        try {
            dto.getClass().getMethod("setComments", List.class).invoke(dto, comments);
        } catch (Exception ignored) {
        }

        return dto;
    }
}