package ru.practicum.shareit.booking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookings;
    private final ItemRepository items;
    private final UserRepository users;

    private static final Sort SORT_DESC = Sort.by(Sort.Direction.DESC, "start");

    public BookingServiceImpl(BookingRepository bookings, ItemRepository items, UserRepository users) {
        this.bookings = bookings;
        this.items = items;
        this.users = users;
    }

    @Override
    public BookingDto create(Long userId, BookingCreateDto dto) {
        User booker = users.findById(userId).orElseThrow(() -> new NotFoundException("user not found"));
        Item item = items.findById(dto.getItemId()).orElseThrow(() -> new NotFoundException("item not found"));

        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("owner can't book own item");
        }
        if (item.getAvailable() == null || !item.getAvailable()) {
            throw new BadRequestException("item is not available");
        }
        if (dto.getStart() == null || dto.getEnd() == null || !dto.getEnd().isAfter(dto.getStart())) {
            throw new BadRequestException("invalid time window");
        }

        Booking b = new Booking();
        b.setItem(item);
        b.setBooker(booker);
        b.setStart(dto.getStart());
        b.setEnd(dto.getEnd());
        b.setStatus(BookingStatus.WAITING);

        return BookingMapper.toDto(bookings.save(b));
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking b = bookings.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking not found"));

        if (!b.getItem().getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("only owner can approve/reject");
        }
        if (b.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("already decided");
        }

        b.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(bookings.save(b));
    }

    @Override
    public BookingDto get(Long userId, Long bookingId) {
        Booking b = bookings.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("booking not found"));
        if (!b.getBooker().getId().equals(userId) && !b.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("not allowed");
        }
        return BookingMapper.toDto(b);
    }

    @Override
    public List<BookingDto> getForBooker(Long userId, String stateStr) {
        users.findById(userId).orElseThrow(() -> new NotFoundException("user not found"));
        return selectByState(true, userId, stateStr).stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getForOwner(Long ownerId, String stateStr) {
        users.findById(ownerId).orElseThrow(() -> new NotFoundException("user not found"));
        return selectByState(false, ownerId, stateStr).stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

    private List<Booking> selectByState(boolean byBooker, Long id, String stateStr) {
        String s = stateStr == null ? "ALL" : stateStr.toUpperCase();
        LocalDateTime now = LocalDateTime.now();
        if (byBooker) {
            return switch (s) {
                case "CURRENT" -> bookings.findByBooker_IdAndStartBeforeAndEndAfter(id, now, now, SORT_DESC);
                case "PAST"    -> bookings.findByBooker_IdAndEndBefore(id, now, SORT_DESC);
                case "FUTURE"  -> bookings.findByBooker_IdAndStartAfter(id, now, SORT_DESC);
                case "WAITING" -> bookings.findByBooker_IdAndStatus(id, BookingStatus.WAITING, SORT_DESC);
                case "REJECTED"-> bookings.findByBooker_IdAndStatus(id, BookingStatus.REJECTED, SORT_DESC);
                case "ALL"     -> bookings.findByBooker_Id(id, SORT_DESC);
                default -> throw new BadRequestException("Unknown state: " + s);
            };
        } else {
            return switch (s) {
                case "CURRENT" -> bookings.findByItem_Owner_IdAndStartBeforeAndEndAfter(id, now, now, SORT_DESC);
                case "PAST"    -> bookings.findByItem_Owner_IdAndEndBefore(id, now, SORT_DESC);
                case "FUTURE"  -> bookings.findByItem_Owner_IdAndStartAfter(id, now, SORT_DESC);
                case "WAITING" -> bookings.findByItem_Owner_IdAndStatus(id, BookingStatus.WAITING, SORT_DESC);
                case "REJECTED"-> bookings.findByItem_Owner_IdAndStatus(id, BookingStatus.REJECTED, SORT_DESC);
                case "ALL"     -> bookings.findByItem_Owner_Id(id, SORT_DESC);
                default -> throw new BadRequestException("Unknown state: " + s);
            };
        }
    }
}