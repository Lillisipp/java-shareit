package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestHeader(USER_HEADER) Long userId,
                          @RequestBody ItemCreateDto dto) {
        return itemService.create(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemUpdateDto patch) {
        return itemService.update(userId, itemId, patch);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(USER_HEADER) Long userId,
                           @PathVariable Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(USER_HEADER) Long ownerID,
                       @PathVariable Long itemId) {
        itemService.delete(ownerID, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(USER_HEADER) Long requesterId,
                                @RequestParam String text) {
        return itemService.search(requesterId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_HEADER) Long userID,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentCreateDto dto) {
        return itemService.addComment(userID, itemId, dto);
    }

    @GetMapping
    public List<ItemOwnerDto> findAllItemsByUser(@RequestHeader(USER_HEADER) Long ownerId,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "20") int size) {
        return itemService.findAllByOwnerWithBookings(ownerId, from, size);
    }

}
