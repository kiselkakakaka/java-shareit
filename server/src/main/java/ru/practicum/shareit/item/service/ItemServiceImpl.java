package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final CommentRepository commentRepo;
    private final BookingRepository bookingRepo;
    private final ItemRequestRepository requestRepo;

    private static PageRequest page(int from, int size) {
        return PageRequest.of(from / size, size);
    }

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemDto dto) {
        User owner = userRepo.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found: " + ownerId));

        ItemRequest request = null;
        if (dto.getRequestId() != null) {
            request = requestRepo.findById(dto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(
                            "Item request not found: " + dto.getRequestId()
                    ));
        }

        Item item = ItemMapper.toModel(dto, owner, request);
        item = itemRepo.save(item);

        return ItemMapper.toDto(item, null, null, List.of());
    }

    @Override
    @Transactional
    public ItemDto update(Long ownerId, Long itemId, ItemDto dto) {
        Item i = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        if (!i.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Only owner can update item");
        }

        if (dto.getName() != null) {
            i.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            i.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            i.setAvailable(dto.getAvailable());
        }

        itemRepo.save(i);
        return getById(ownerId, i.getId());
    }

    @Override
    public ItemDto getById(Long requesterId, Long itemId) {
        Item i = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        List<Comment> comments = commentRepo.findAllByItemIdOrderByCreatedDesc(itemId);

        if (i.getOwner() != null && i.getOwner().getId().equals(requesterId)) {
            LocalDateTime now = LocalDateTime.now();
            Booking last = bookingRepo
                    .findFirstByItemIdAndStartBeforeOrderByEndDesc(itemId, now)
                    .orElse(null);
            Booking next = bookingRepo
                    .findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, now)
                    .orElse(null);
            return ItemMapper.toDto(i, last, next, comments);
        } else {
            return ItemMapper.toDto(i, null, null, comments);
        }
    }

    @Override
    public List<ItemDto> getOwnerItems(Long ownerId, int from, int size) {
        return itemRepo.findAllByOwnerId(ownerId, page(from, size))
                .map(i -> getById(ownerId, i.getId()))
                .getContent();
    }

    @Override
    public List<ItemDto> search(String text, int from, int size) {
        if (text == null || text.isBlank()) return List.of();
        return itemRepo.searchAvailable(text, page(from, size))
                .map(i -> ItemMapper.toDto(i, null, null, List.of()))
                .getContent();
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, String text) {
        User author = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        boolean hadApprovedBooking = !bookingRepo
                .findByItemIdAndBookerIdAndStatusAndEndBefore(
                        itemId, userId, BookingStatus.APPROVED, LocalDateTime.now()
                )
                .isEmpty();

        if (!hadApprovedBooking) {
            throw new ValidationException("User has not completed an approved booking of this item");
        }

        Comment c = Comment.builder()
                .text(text)
                .author(author)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        return ItemMapper.toCommentDto(commentRepo.save(c));
    }
}