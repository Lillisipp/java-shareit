package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByItem_IdAndStatusAndStartLessThanAndEndGreaterThan(
            Long itemId, BookingStatus status, LocalDateTime start, LocalDateTime end);

    Optional<Booking> findByItem_Id(Long itemId);

    @Query("""
              select b from Booking b
              join fetch b.booker
              join fetch b.item i
              join fetch i.owner
              where b.id = :id
            """)
    Optional<Booking> findDetailedById(Long id);

    //поиск по state
    Page<Booking> findByBooker_Id(Long userId, Pageable p);

    Page<Booking> findByBooker_IdAndStartBeforeAndEndAfter(Long userId, LocalDateTime now1, LocalDateTime now2, Pageable p);

    Page<Booking> findByBooker_IdAndEndBefore(Long userId, LocalDateTime now, Pageable p);

    Page<Booking> findByBooker_IdAndStartAfter(Long userId, LocalDateTime now, Pageable p);

    Page<Booking> findByBooker_IdAndStatus(Long userId, BookingStatus status, Pageable p);

    Page<Booking> findByItem_Owner_Id(Long ownerId, Pageable p);

    Page<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime now1, LocalDateTime now2, Pageable p);

    Page<Booking> findByItem_Owner_IdAndEndBefore(Long ownerId, LocalDateTime now, Pageable p);

    Page<Booking> findByItem_Owner_IdAndStartAfter(Long ownerId, LocalDateTime now, Pageable p);

    Page<Booking> findByItem_Owner_IdAndStatus(Long ownerId, BookingStatus status, Pageable p);


    @Query(value = """
              select distinct on (b.id_item) b.*
              from bookings b
              where b.id_item in (:ids)
                and b.status = :st
                and b.start_time < :now
              order by b.id_item, b.end_time desc
            """, nativeQuery = true)
    List<Booking> findLastByItemIds(List<Long> ids,
                                    LocalDateTime now,
                                    String st);

    @Query(value = """
              select distinct on (b.id_item) b.*
              from bookings b
              where b.id_item in (:ids)
                and b.status = :st
                and b.start_time > :now
              order by b.id_item, b.start_time
            """, nativeQuery = true)
    List<Booking> findNextByItemIds(List<Long> ids,
                                    LocalDateTime now,
                                    String st);


    boolean existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
            Long bookerId, Long itemId, BookingStatus status, LocalDateTime endBefore);

}
