package ru.practicum.shareit.booking.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class BookingRepositoryImpl implements BookingRepository {
    private final Map<Long, Booking> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public Booking create(Booking b) {
        if (b.getId() == null) {
            b.setId(seq.getAndIncrement());
        }
        store.put(b.getId(), b);
        return store.get(b.getId());
    }

    @Override
    public Optional<Booking> findById(Long id) {
        var v = store.get(id);
        return Optional.ofNullable(v == null ? null : v);
    }

    @Override
    public List<Booking> findAllByBookerId(Long bookerId) {
        return store.values().stream()
                .filter(x -> x.getBooker() != null && Objects.equals(x.getBooker().getId(), bookerId))
                .sorted(Comparator.comparing(Booking::getStart))
                .toList();
    }

    @Override
    public List<Booking> findAllOwnerId(Long ownerId) {
        return store.values()
                .stream()
                .filter(x -> x.getItem() != null && x.getItem().getOwner() != null
                        && Objects.equals(x.getItem().getOwner().getId(), ownerId))
                .sorted(Comparator.comparing(Booking::getStart))
                .toList();
    }

    @Override
    public boolean hasApprovedOverlap(Long itemId, LocalDateTime start, LocalDateTime end) {
        return store.values().stream()
                .filter(b -> b.getItem() != null && Objects.equals(b.getItem().getId(), itemId))
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .anyMatch(b -> overlaps(b.getStart(), b.getEnd(), start, end));
    }

    private boolean overlaps(LocalDateTime aStart, LocalDateTime aEnd,
                             LocalDateTime bStart, LocalDateTime bEnd) {
        // пересечение полуинтервалов [aStart, aEnd) и [bStart, bEnd)
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }
}