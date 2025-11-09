package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImp implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;
    private final BookingRepository bookingRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepo;
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemDto create(Long ownerId, ItemCreateDto dto) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("владелец не найден"));

        Item item = itemMapper.toItemFromCreateDto(dto, owner);

        if (dto.getRequestId() != null) {
            boolean exist = requestRepository.existsById(dto.getRequestId());
            if (!exist) {
                throw new GlobalExceptionHandler.NotFoundException("Запрос на вещь не найден");
            }
            item.setRequest(dto.getRequestId());
        }
        Item saved = itemRepository.save(item);
        return itemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemUpdateDto dto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("item not found"));
        if (item.getOwner() == null || !item.getOwner().getId().equals(ownerId)) {
            throw new GlobalExceptionHandler.NotFoundException("only owner can update item");
        }
        itemMapper.updateItemFromUpdateDto(dto, item);
        Item saved = itemRepository.save(item);
        return itemMapper.toItemDto(saved);
    }

    @Override
    public ItemDto getById(Long requesterId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("item not found"));

        ItemDto dto = itemMapper.toItemDto(item);

        var comments = commentRepo.findByItem_IdOrderByCreatedAsc(itemId);
        dto.setComments(commentMapper.toDto(comments));

        dto.setLastBooking(null);
        dto.setNextBooking(null);

        return dto;
    }


    @Override
    public List<ItemDto> search(Long requesterId, String text) {
        if (!StringUtils.hasText(text)) return List.of();
        return itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public void delete(Long ownerID, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("item not found"));
        if (item.getOwner() == null || !item.getOwner().getId().equals(ownerID)) {
            throw new IllegalStateException("only owner can delete item");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemOwnerDto> findAllByOwnerWithBookings(Long ownerId, int from, int size) {
        if (size <= 0) return List.of();

        var page = itemRepository.findByOwner_Id(
                ownerId, PageRequest.of(from / size, size));
        if (page.isEmpty()) return List.of();

        var items = page.getContent();
        var itemIds = items.stream()
                .map(Item::getId)
                .toList();

        var now = LocalDateTime.now();
        var status = BookingStatus.APPROVED.name();

        var last = bookingRepository.findLastByItemIds(itemIds, now, status)
                .stream()
                .collect(Collectors.toMap(
                        b -> b.getItem().getId(),
                        b -> b,
                        (recordOne, recordTwo) -> recordOne));
        var next = bookingRepository.findNextByItemIds(itemIds, now, status)
                .stream()
                .collect(Collectors.toMap(b -> b.getItem().getId(),
                        b -> b,
                        (recordOne, recordTwo) -> recordOne));

        var commentsByItemId = commentRepo.findByItemIdInOrderByCreatedAsc(itemIds)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));


        return items.stream().map(it -> {
            var dto = itemMapper.toOwnerDto(it);

            var lb = last.get(it.getId());
            if (lb != null) {
                dto.setLastBooking(new BookingShortDto(
                        lb.getId(),
                        lb.getBooker().getId(),
                        lb.getStart(),
                        lb.getEnd()
                ));
            }
            var nb = next.get(it.getId());
            if (nb != null) {
                dto.setNextBooking(new BookingShortDto(
                        nb.getId(),
                        nb.getBooker().getId(),
                        nb.getStart(),
                        nb.getEnd()
                ));
            }
            var comms = commentsByItemId.getOrDefault(it.getId(), List.of());
            dto.setComments(
                    comms.stream().map(commentMapper::toDto).toList()
            );

            return dto;
        }).toList();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentCreateDto dto) {
        if (dto == null || dto.getText() == null || dto.getText().isBlank()) {
            throw new IllegalStateException("комментарий пуст");
        }
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("user not found"));
        var item = itemRepository.findById(itemId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("item not found"));

        LocalDateTime now = LocalDateTime.now();
        log.info("addComment: userId: {}, itemId: {}, now: {}", userId, itemId, now);

        boolean allowed = bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, now
        );

        if (!allowed) {
            throw new IllegalStateException("пользователь не брал товар");
        }

        var comment = new Comment();
        comment.setText(dto.getText().trim());
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(now);

        return commentMapper.toDto(commentRepo.save(comment));
    }
}
