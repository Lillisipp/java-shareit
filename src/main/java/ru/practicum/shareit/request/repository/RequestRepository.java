package ru.practicum.shareit.request.repository;

import ru.practicum.shareit.request.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository {
    ItemRequest save(ItemRequest user);

    Optional<ItemRequest> findById(Long id);

    List<ItemRequest> findByRequestorId(Long userid);

}
