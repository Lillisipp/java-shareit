package ru.practicum.shareit.request.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class RequestRepositoryImpl implements RequestRepository {

    private final Map<Long, ItemRequest> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);

    @Override
    public ItemRequest save(ItemRequest user) {
        if (user.getId() == null) {
            user.setId(seq.getAndIncrement());
        }
        store.put(user.getId(), user);
        return store.get(user.getId());
    }

    @Override
    public ItemRequest update(Long id) {
        return null;
    }

    @Override
    public Optional<ItemRequest> findById(Long id) {
        var request = store.get(id);
        return Optional.ofNullable(request);
    }

    @Override
    public List<ItemRequest> findByRequestorId(Long requestorId) {
        return store.values().stream()
                .filter(it -> it.getRequestor() != null && Objects.equals(it.getRequestor().getId(), requestorId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .toList();
    }

    @Override
    public List<ItemRequest> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .toList();
    }
}
