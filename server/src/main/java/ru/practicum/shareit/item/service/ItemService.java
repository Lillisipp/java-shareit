package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

@Service
public interface ItemService {

    ItemDto create(Long ownerId, ItemCreateDto dto);

    ItemDto update(Long ownerId, Long itemId, ItemUpdateDto dto);

    ItemDto getById(Long requesterId, Long itemId);

    List<ItemDto> search(Long requesterId, String text);

    void delete(Long ownerID, Long itemId);

    List<ItemOwnerDto> findAllByOwnerWithBookings(Long ownerId, int from, int size);

    CommentDto addComment(Long userId, Long itemId, CommentCreateDto dto);
}