package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_Id(Long bookerId, Sort sort);
    List<Booking> findByBooker_IdAndStartBeforeAndEndAfter(Long id, LocalDateTime now1, LocalDateTime now2, Sort s);
    List<Booking> findByBooker_IdAndEndBefore(Long id, LocalDateTime t, Sort s);
    List<Booking> findByBooker_IdAndStartAfter(Long id, LocalDateTime t, Sort s);
    List<Booking> findByBooker_IdAndStatus(Long id, BookingStatus status, Sort s);

    List<Booking> findByItem_Owner(Long ownerId, Sort sort);
    List<Booking> findByItem_OwnerAndStartBeforeAndEndAfter(Long id, LocalDateTime n1, LocalDateTime n2, Sort s);
    List<Booking> findByItem_OwnerAndEndBefore(Long id, LocalDateTime t, Sort s);
    List<Booking> findByItem_OwnerAndStartAfter(Long id, LocalDateTime t, Sort s);
    List<Booking> findByItem_OwnerAndStatus(Long id, BookingStatus status, Sort s);

    List<Booking> findTop1ByItem_IdAndStatusAndStartBeforeOrderByStartDesc(Long itemId, BookingStatus st, LocalDateTime now);
    List<Booking> findTop1ByItem_IdAndStatusAndStartAfterOrderByStartAsc(Long itemId, BookingStatus st, LocalDateTime now);

    boolean existsByBooker_IdAndItem_IdAndEndBeforeAndStatus(Long bookerId, Long itemId, LocalDateTime t, BookingStatus st);
}
