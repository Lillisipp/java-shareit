package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public ItemDto create(Long ownerId, ItemCreateDto dto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("owner not found"));
        Item item = itemMapper.toItemFromCreateDto(dto, owner);
        Item saved = itemRepository.save(item);
        return itemMapper.toItemDto(saved);
    }

    public ItemDto update(Long ownerId, Long itemId, ItemUpdateDto dto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("item not found"));
        if (item.getOwner() == null || !item.getOwner().getId().equals(ownerId)) {
            throw new IllegalStateException("only owner can update item");
        }
        itemMapper.updateItemFromUpdateDto(dto, item);
        Item saved = itemRepository.save(item);
        return itemMapper.toItemDto(saved);
    }

    public ItemDto getById(Long requesterId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("item not found"));
        return itemMapper.toItemDto(item);
    }

    public List<ItemDto> findAllByOwner(Long ownerId) {
        return itemRepository.findAllByOwner(ownerId).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    public List<ItemDto> search(Long requesterId, String text) {
        if (!StringUtils.hasText(text)) return List.of(); // по ТЗ пустой запрос -> []
        return itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    public void delete(Long ownerID, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("item not found"));
        if (item.getOwner() == null || !item.getOwner().getId().equals(ownerID)) {
            throw new IllegalStateException("only owner can delete item");
        }
        itemRepository.deleteById(itemId);
    }
}