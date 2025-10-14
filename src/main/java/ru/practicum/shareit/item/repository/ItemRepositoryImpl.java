package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong generator = new AtomicLong(0);

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(generator.incrementAndGet());
        }
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Optional<Item> findById(Long id) {
        Item found = items.get(id);
        return Optional.ofNullable(found == null ? null : found);
    }

    @Override
    public List<Item> findAllByOwner(Long ownerId) {
        return items.values().stream()
                .filter(i -> i.getOwner() != null && Objects.equals(i.getOwner().getId(), ownerId))
                .sorted(Comparator.comparing(Item::getId))
                .toList();
    }

    @Override
    public List<Item> search(String text) {
        String q = text == null ? "" : text.trim().toLowerCase();
        if (q.isEmpty()) return List.of();             // по ТЗ пустой запрос -> пустой список
        return items.values().stream()
                .filter(i -> Boolean.TRUE.equals(i.getAvailable()))
                .filter(i -> contains(i.getName(), q) || contains(i.getDescription(), q))
                .sorted(Comparator.comparing(Item::getId))
                .toList();
    }

    @Override
    public void deleteById(Long itemID) {
        items.remove(itemID);
    }


    private boolean contains(String s, String q) {
        return s != null && s.toLowerCase().contains(q);
    }

}
