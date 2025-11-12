package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;

    private User owner;
    private User booker;
    private Item item1;
    private Item item2;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        owner = em.persist(new User(null, "Owner", "owner@mail.com"));
        booker = em.persist(new User(null, "Booker", "booker@mail.com"));
        item1 = em.persist(new Item(null, "Drill", "Powerful", true, owner, null));
        item2 = em.persist(new Item(null, "Saw", "Sharp", true, owner, null));

        now = LocalDateTime.now().withNano(0);

        // past (APPROVED) for item1
        em.persist(new Booking(null,
                now.minusDays(3),
                now.minusDays(1),
                item1, booker, BookingStatus.APPROVED));

        // current (APPROVED) for item1
        em.persist(new Booking(null,
                now.minusHours(1),
                now.plusHours(2),
                item1, booker, BookingStatus.APPROVED));

        // future (APPROVED) for item1
        em.persist(new Booking(null,
                now.plusDays(1),
                now.plusDays(2),
                item1, booker, BookingStatus.APPROVED));

        // past (REJECTED) for item2
        em.persist(new Booking(null,
                now.minusDays(5),
                now.minusDays(4),
                item2, booker, BookingStatus.REJECTED));

        // future (WAITING) for item2
        em.persist(new Booking(null,
                now.plusDays(3),
                now.plusDays(4),
                item2, booker, BookingStatus.WAITING));

        em.flush();
    }

    @Test
    void findByBooker_Id_pagedAndSorted() {
        Page<Booking> page = bookingRepository.findByBooker_Id(
                booker.getId(),
                PageRequest.of(0, 10, Sort.by("start").descending())
        );

        assertEquals(5, page.getTotalElements());
        List<Booking> res = page.getContent();
        assertTrue(res.get(0).getStart().isAfter(res.get(1).getStart()));
    }

    @Test
    void findByItem_Owner_Id_ownerSeesAll() {
        Page<Booking> page = bookingRepository.findByItem_Owner_Id(
                owner.getId(),
                PageRequest.of(0, 10, Sort.by("start").descending())
        );

        assertEquals(5, page.getTotalElements());
        assertEquals(owner.getId(), page.getContent().get(0).getItem().getOwner().getId());
    }

    @Test
    void findByBooker_IdAndStatus_filtersByStatus() {
        Page<Booking> page = bookingRepository.findByBooker_IdAndStatus(
                booker.getId(), BookingStatus.REJECTED,
                PageRequest.of(0, 10, Sort.by("start").descending())
        );

        assertEquals(1, page.getNumberOfElements());
        Booking b = page.getContent().get(0);
        assertEquals(BookingStatus.REJECTED, b.getStatus());
        assertEquals(item2.getId(), b.getItem().getId());
    }

    @Test
    void findByBooker_IdAndStartBeforeAndEndAfter_currentState() {
        Page<Booking> page = bookingRepository
                .findByBooker_IdAndStartBeforeAndEndAfter(
                        booker.getId(), now, now,
                        PageRequest.of(0, 10, Sort.by("start").descending())
                );

        assertEquals(1, page.getTotalElements());
        Booking b = page.getContent().get(0);
        assertTrue(b.getStart().isBefore(now));
        assertTrue(b.getEnd().isAfter(now));
    }

    @Test
    void findByBooker_IdAndEndBefore_pastState() {
        Page<Booking> page = bookingRepository
                .findByBooker_IdAndEndBefore(
                        booker.getId(), now,
                        PageRequest.of(0, 10, Sort.by("start").descending())
                );

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().stream().allMatch(b -> b.getEnd().isBefore(now)));
    }

    @Test
    void findByBooker_IdAndStartAfter_futureState() {
        Page<Booking> page = bookingRepository
                .findByBooker_IdAndStartAfter(
                        booker.getId(), now,
                        PageRequest.of(0, 10, Sort.by("start").descending())
                );

        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().stream().allMatch(b -> b.getStart().isAfter(now)));
    }

    @Test
    void existsByItem_IdAndStatusAndStartLessThanAndEndGreaterThan_currentApprovedExists() {
        boolean exists = bookingRepository
                .existsByItem_IdAndStatusAndStartLessThanAndEndGreaterThan(
                        item1.getId(), BookingStatus.APPROVED, now, now);

        assertTrue(exists);
    }

    @Test
    void existsByBooker_IdAndItem_IdAndStatusAndEndBefore_finishedApprovedExists() {
        boolean exists = bookingRepository
                .existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                        booker.getId(), item1.getId(), BookingStatus.APPROVED, now);

        assertTrue(exists);
    }
}
