package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(seq.getAndIncrement());
        }
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id)).map(this::copy);
    }

    @Override
    public List<User> findAll() {
        return users.values()
                .stream()
                .map(this::copy)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.values()
                .stream()
                .anyMatch(user -> user.getEmail()
                        .equalsIgnoreCase(email));
    }

    @Override
    public boolean existsByEmailOrNot(String email, Long id) {
        return users.values()
                .stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email)
                        && !user.getId().equals(id));
    }

    private User copy(User s) {
        User c = new User();
        c.setId(s.getId());
        c.setName(s.getName());
        c.setEmail(s.getEmail());
        return c;
    }
}

