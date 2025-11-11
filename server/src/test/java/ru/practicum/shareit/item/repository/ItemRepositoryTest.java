package ru.practicum.shareit.item.repository;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;

    private User owner1;
    private User owner2;
    private Item i1; // доступен, совпадение по name
    private Item i2; // доступен, совпадение по description
    private Item i3; // НЕдоступен, совпадение есть, но его быть не должно в search
    private Item i4; // другой владелец
    private Item i5; // с requestId = 100L
    private Item i6; // с requestId = 200L

    @BeforeEach
    void setUp() {
        owner1 = em.persist(new User(null, "Alice", "alice@mail.com"));
        owner2 = em.persist(new User(null, "Bob", "bob@mail.com"));

        i1 = em.persist(new Item(null, "Super Drill", "Power tool", true, owner1, null));
        i2 = em.persist(new Item(null, "Hammer", "drill-compatible adapter", true, owner1, null));
        i3 = em.persist(new Item(null, "Mini drill", "Old model", false, owner1, null)); // available=false
        i4 = em.persist(new Item(null, "Saw", "Sharp hand saw", true, owner2, null));
        i5 = em.persist(new Item(null, "Glue", "Strong glue", true, owner1, 100L));
        i6 = em.persist(new Item(null, "Tape", "Duct tape", true, owner1, 200L));

        em.flush();
        em.clear();
    }

    @Test
    void search_returnsOnlyAvailable_caseInsensitive_inNameOrDescription() {
        // text = "DRILL" должен найти i1 по name и i2 по description; i3 отфильтруется из-за available=false
        List<Item> res = itemRepository.search("DRILL");

        assertEquals(2, res.size());
        assertTrue(res.stream().anyMatch(it -> it.getId().equals(i1.getId())));
        assertTrue(res.stream().anyMatch(it -> it.getId().equals(i2.getId())));
        assertTrue(res.stream().noneMatch(it -> it.getId().equals(i3.getId())));
    }

    @Test
    void findByOwner_Id_entityGraphLoadsOwner_usesPagination() {
        Page<Item> page = itemRepository.findByOwner_Id(
                owner1.getId(),
                PageRequest.of(0, 10, Sort.by("name").ascending())
        );

        // у owner1 четыре вещи: i1, i2, i3, i5, i6 -> всего 5 (i4 у owner2)
        assertEquals(5, page.getTotalElements());
        page.getContent().forEach(it -> {
            assertEquals(owner1.getId(), it.getOwner().getId());
            // проверяем, что @EntityGraph подгрузил owner без ленивой прокси
            assertTrue(Hibernate.isInitialized(it.getOwner()));
        });
    }

    @Test
    void findByRequestIn_returnsOnlyThoseWithRequestIds() {
        List<Item> res = itemRepository.findByRequestIn(List.of(100L, 200L));

        assertEquals(2, res.size());
        assertTrue(res.stream().anyMatch(it -> it.getId().equals(i5.getId())));
        assertTrue(res.stream().anyMatch(it -> it.getId().equals(i6.getId())));
        assertTrue(res.stream().noneMatch(it -> it.getId().equals(i1.getId())));
    }

    @Test
    void findByRequest_returnsSingleRequestGroup() {
        List<Item> res = itemRepository.findByRequest(100L);

        assertEquals(1, res.size());
        assertEquals(i5.getId(), res.get(0).getId());
    }
}
