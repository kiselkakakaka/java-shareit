package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;

    private static PageRequest page(int from, int size) {
        return PageRequest.of(from / size, size, Sort.by("start").descending());
    }

    @Override
    @Transactional
    public BookingDto create(Long bookerId, BookingShortDto dto) {
        User booker = userRepo.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found: " + bookerId));

        Item item = itemRepo.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found: " + dto.getItemId()));

        if (item.getOwner() != null && item.getOwner().getId().equals(bookerId)) {
            throw new NotFoundException("Owner cannot book own item");
        }
        if (item.getAvailable() == null || !item.getAvailable()) {
            throw new ValidationException("Item not available");
        }

        LocalDateTime start = dto.getStart();
        LocalDateTime end = dto.getEnd();
        if (start == null || end == null) {
            throw new ValidationException("Dates must not be null");
        }
        if (!start.isBefore(end)) {
            throw new ValidationException("Start must be before end");
        }
        if (start.isBefore(LocalDateTime.now()) || end.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Dates must be in future");
        }

        Booking booking = BookingMapper.toModel(dto, booker, item);
        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toDto(bookingRepo.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Only owner can approve");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Booking already decided");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(bookingRepo.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking b = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        Long ownerId = b.getItem().getOwner().getId();
        Long bookerId = b.getBooker().getId();
        if (!userId.equals(ownerId) && !userId.equals(bookerId)) {
            throw new NotFoundException("Booking not available for user");
        }
        return BookingMapper.toDto(b);
    }

    @Override
    public List<BookingDto> getAll(Long userId, BookingState state, Integer from, Integer size) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
        LocalDateTime now = LocalDateTime.now();
        return switch (state) {
            case ALL -> bookingRepo.findAllByBookerId(userId, page(from, size))
                    .map(BookingMapper::toDto).getContent();
            case CURRENT -> bookingRepo.findCurrentByBookerId(userId, now, page(from, size))
                    .map(BookingMapper::toDto).getContent();
            case PAST -> bookingRepo.findPastByBookerId(userId, now, page(from, size))
                    .map(BookingMapper::toDto).getContent();
            case FUTURE -> bookingRepo.findFutureByBookerId(userId, now, page(from, size))
                    .map(BookingMapper::toDto).getContent();
            case WAITING -> bookingRepo.findByBookerIdAndStatus(userId, BookingStatus.WAITING, page(from, size))
                    .map(BookingMapper::toDto).getContent();
            case REJECTED -> bookingRepo.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, page(from, size))
                    .map(BookingMapper::toDto).getContent();
        };
    }

    @Override
    public List<BookingDto> getOwnerBookings(Long ownerId, BookingState state, Integer from, Integer size) {
        if (!userRepo.existsById(ownerId)) {
            throw new NotFoundException("User not found: " + ownerId);
        }
        LocalDateTime now = LocalDateTime.now();
        return switch (state) {
            case ALL -> bookingRepo.findAllByOwnerId(ownerId, page(from, size))
                    .map(BookingMapper::toDto).getContent();
            case CURRENT -> bookingRepo.findCurrentByOwnerId(ownerId, now, page(from, size))
                    .map(BookingMapper::toDto).getContent();
            case PAST -> bookingRepo.findPastByOwnerId(ownerId, now, page(from, size))
                    .map(BookingMapper::toDto).getContent();
            case FUTURE -> bookingRepo.findFutureByOwnerId(ownerId, now, page(from, size))
                    .map(BookingMapper::toDto).getContent();
            case WAITING -> bookingRepo.findByOwnerIdAndStatus(ownerId, BookingStatus.WAITING, page(from, size))
                    .map(BookingMapper::toDto).getContent();
            case REJECTED -> bookingRepo.findByOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, page(from, size))
                    .map(BookingMapper::toDto).getContent();
        };
    }
}