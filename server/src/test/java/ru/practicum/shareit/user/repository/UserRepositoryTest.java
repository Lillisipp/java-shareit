package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    UserRepository repo;

    User u1;
    User u2;

    @BeforeEach
    void setUp() {
        u1 = em.persist(new User(null, "Alice", "alice@mail.com"));
        u2 = em.persist(new User(null, "Bob", "bob@mail.com"));
        em.flush();
    }

    @Test
    void existsByEmail_trueAndFalse() {
        assertTrue(repo.existsByEmail("alice@mail.com"));
        assertFalse(repo.existsByEmail("none@mail.com"));
    }

    @Test
    void existsByEmailAndId_exactMatchOnly() {
        assertTrue(repo.existsByEmailAndId("alice@mail.com", u1.getId()));
        assertFalse(repo.existsByEmailAndId("alice@mail.com", u2.getId())); // email не u2
        assertFalse(repo.existsByEmailAndId("none@mail.com", u1.getId()));  // email отсутствует
    }
}
