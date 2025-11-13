package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findFirstByItemIdAndStartBeforeOrderByEndDesc(Long itemId, LocalDateTime now);
    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    List<Booking> findByItemIdAndBookerIdAndStatusAndEndBefore(
            Long itemId, Long bookerId, BookingStatus status, LocalDateTime before);

    Page<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    @Query("""
           select b from Booking b
           where b.booker.id = :bookerId
             and :now between b.start and b.end
           order by b.start desc
           """)
    Page<Booking> findCurrentByBookerId(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query("""
           select b from Booking b
           where b.booker.id = :bookerId
             and b.end < :now
           order by b.start desc
           """)
    Page<Booking> findPastByBookerId(Long bookerId, LocalDateTime now, Pageable pageable);

    @Query("""
           select b from Booking b
           where b.booker.id = :bookerId
             and b.start > :now
           order by b.start desc
           """)
    Page<Booking> findFutureByBookerId(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    @Query("""
           select b from Booking b
           where b.item.owner.id = :ownerId
           order by b.start desc
           """)
    Page<Booking> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query("""
           select b from Booking b
           where b.item.owner.id = :ownerId
             and :now between b.start and b.end
           order by b.start desc
           """)
    Page<Booking> findCurrentByOwnerId(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query("""
           select b from Booking b
           where b.item.owner.id = :ownerId
             and b.end < :now
           order by b.start desc
           """)
    Page<Booking> findPastByOwnerId(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query("""
           select b from Booking b
           where b.item.owner.id = :ownerId
             and b.start > :now
           order by b.start desc
           """)
    Page<Booking> findFutureByOwnerId(Long ownerId, LocalDateTime now, Pageable pageable);

    @Query("""
           select b from Booking b
           where b.item.owner.id = :ownerId
             and b.status = :status
           order by b.start desc
           """)
    Page<Booking> findByOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);
}