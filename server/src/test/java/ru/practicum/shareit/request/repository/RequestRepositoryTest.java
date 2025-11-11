package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class RequestRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    ItemRequestRepository repo;

    private Long u1;
    private Long u2;
    private LocalDateTime now;
    private ItemRequest r1u1Old;
    private ItemRequest r2U1New;
    private ItemRequest r3U2;

    @BeforeEach
    void setUp() {
        u1 = 10L;
        u2 = 20L;
        now = LocalDateTime.now().withNano(0);

        r1u1Old = em.persist(new ItemRequest(null, "need drill", u1, now.minusDays(2)));
        r2U1New = em.persist(new ItemRequest(null, "need saw", u1, now.minusHours(1)));
        r3U2 = em.persist(new ItemRequest(null, "need tape", u2, now.minusDays(1)));

        em.flush();
        em.clear();
    }

    @Test
    void findByRequestor_returnsAllForUser() {
        List<ItemRequest> res = repo.findByRequestor(u1);

        assertEquals(2, res.size());
        assertTrue(res.stream().allMatch(r -> r.getRequestor().equals(u1)));
    }

    @Test
    void findByRequestorOrderByCreatedDesc_sortedNewestFirst() {
        List<ItemRequest> res = repo.findByRequestorOrderByCreatedDesc(u1);

        assertEquals(2, res.size());
        assertTrue(res.get(0).getCreated().isAfter(res.get(1).getCreated()));
        assertEquals(u1, res.get(0).getRequestor());
    }

    @Test
    void findByRequestorNot_excludesUser_andPaged() {
        Page<ItemRequest> page = repo.findByRequestorNot(u1, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
        ItemRequest only = page.getContent().get(0);
        assertEquals(r3U2.getId(), only.getId());
        assertEquals(u2, only.getRequestor());
    }
}
