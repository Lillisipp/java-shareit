package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

@Service
public interface ItemService {

    public ItemDto create(Long ownerId, ItemCreateDto dto);

    public ItemDto update(Long ownerId, Long itemId, ItemUpdateDto dto);

    public ItemDto getById(Long requesterId, Long itemId);

    public List<ItemDto> findAllByOwner(Long ownerId);

    public List<ItemDto> search(Long requesterId, String text);

    public void delete(Long ownerID, Long itemId);
}