package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking create(Booking b);

    Optional<Booking> findById(Long id);

    List<Booking> findAllByBookerId(Long bookerId);

    List<Booking> findAllOwnerId(Long ownerId);

    boolean hasApprovedOverlap(Long itemId, LocalDateTime start, LocalDateTime end);
}
