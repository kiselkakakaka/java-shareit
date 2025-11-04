package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + ownerId));

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BadRequestException("name must not be blank");
        }
        if (dto.getAvailable() == null) {
            throw new BadRequestException("available must not be null");
        }

        Item toSave = ItemMapper.fromDto(dto, ownerId, null);
        toSave.setOwner(owner);

        Item saved = itemRepository.save(toSave);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto patch) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + itemId));

        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new NotFoundException("Вещь не принадлежит пользователю: " + ownerId);
        }

        if (patch.getName() != null && !patch.getName().isBlank()) {
            item.setName(patch.getName());
        }
        if (patch.getDescription() != null) {
            item.setDescription(patch.getDescription());
        }
        if (patch.getAvailable() != null) {
            item.setAvailable(patch.getAvailable());
        }
        if (patch.getRequestId() != null) {
            item.setRequestId(patch.getRequestId());
        }

        Item saved = itemRepository.save(item);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long requesterId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + itemId));

        LocalDateTime now = LocalDateTime.now();

        ItemDto.BookingShort last = null;
        ItemDto.BookingShort next = null;

        if (item.getOwner() != null && Objects.equals(item.getOwner().getId(), requesterId)) {
            Booking lastBooking = bookingRepository
                    .findTop1ByItem_IdAndStatusAndStartBeforeOrderByStartDesc(itemId, BookingStatus.APPROVED, now);
            if (lastBooking != null) {
                last = new ItemDto.BookingShort();
                last.setId(lastBooking.getId());
                last.setBookerId(lastBooking.getBooker().getId());
            }

            Booking nextBooking = bookingRepository
                    .findTop1ByItem_IdAndStatusAndStartAfterOrderByStartAsc(itemId, BookingStatus.APPROVED, now);
            if (nextBooking != null) {
                next = new ItemDto.BookingShort();
                next.setId(nextBooking.getId());
                next.setBookerId(nextBooking.getBooker().getId());
            }
        }

        List<Comment> comments = commentRepository.findByItem_IdOrderByCreatedDesc(itemId);
        List<ItemDto.CommentDto> commentDtos = CommentMapper.toDtoList(comments);

        return ItemMapper.toItemDto(item, last, next, commentDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getOwnerItems(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь не найден: " + ownerId);
        }

        LocalDateTime now = LocalDateTime.now();

        return itemRepository.findByOwner(ownerId).stream()
                .map(item -> {
                    ItemDto.BookingShort last = null;
                    ItemDto.BookingShort next = null;

                    Booking lastBooking = bookingRepository
                            .findTop1ByItem_IdAndStatusAndStartBeforeOrderByStartDesc(item.getId(), BookingStatus.APPROVED, now);
                    if (lastBooking != null) {
                        last = new ItemDto.BookingShort();
                        last.setId(lastBooking.getId());
                        last.setBookerId(lastBooking.getBooker().getId());
                    }

                    Booking nextBooking = bookingRepository
                            .findTop1ByItem_IdAndStatusAndStartAfterOrderByStartAsc(item.getId(), BookingStatus.APPROVED, now);
                    if (nextBooking != null) {
                        next = new ItemDto.BookingShort();
                        next.setId(nextBooking.getId());
                        next.setBookerId(nextBooking.getBooker().getId());
                    }

                    List<Comment> comments = commentRepository.findByItem_IdOrderByCreatedDesc(item.getId());
                    List<ItemDto.CommentDto> commentDtos = CommentMapper.toDtoList(comments);

                    return ItemMapper.toItemDto(item, last, next, commentDtos);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.searchByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto.CommentDto addComment(Long userId, Long itemId, String text) {
        if (text == null || text.isBlank()) {
            throw new BadRequestException("Текст комментария не может быть пустым");
        }

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена: " + itemId));

        LocalDateTime now = LocalDateTime.now();
        boolean hadFinishedApprovedBooking = bookingRepository
                .existsByBooker_IdAndItem_IdAndEndBeforeAndStatus(userId, itemId, now, BookingStatus.APPROVED);

        if (!hadFinishedApprovedBooking) {
            throw new BadRequestException("Оставлять комментарии могут только пользователи с завершённым бронированием этой вещи");
        }

        Comment newComment = new Comment();
        newComment.setText(text);
        newComment.setAuthor(author);
        newComment.setItem(item);
        newComment.setCreated(now);

        Comment saved = commentRepository.save(newComment);
        return CommentMapper.toDto(saved);
    }
}