package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestItemAnswerDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRepository itemRepository;


    @Override
    public ItemRequestDto create(Long userId, RequestCreateDto dto) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("user not found"));
        ItemRequest entity = itemRequestMapper.toEntity(dto, requestor);
        return itemRequestMapper.toDto(requestRepository.save(entity));
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("user not found"));
        ItemRequest r = requestRepository.findById(requestId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("request not found"));

        ItemRequestDto dto = itemRequestMapper.toDto(r);
        // 4) подтягиваем связанные вещи и кладем их в dto.items
        List<Item> items = itemRepository.findByRequest(requestId);
        List<RequestItemAnswerDto> itemDtos = items.stream()
                .map(it -> new RequestItemAnswerDto(
                        it.getId(),
                        it.getName(),
                        it.getOwner() != null ? it.getOwner().getId() : null
                ))
                .toList();

        dto.setItems(itemDtos != null ? itemDtos : java.util.Collections.emptyList());

        return dto;
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("user not found"));
        return itemRequestMapper.toDto(requestRepository.findByRequestor(userId));
    }

    @Override
    public List<ItemRequestDto> getAllOthers(Long userId, int from, int size) {
        if (size <= 0) return null;

        var page = requestRepository
                .findByRequestorNot(userId, PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")));
        if (page.isEmpty()) return List.of();
        var reqs = page.getContent();
        var ids = reqs.stream()
                .map(ItemRequest::getId)
                .toList();
        var items = itemRepository.findByRequestIn(ids);
        var groupReqId = items.stream()
                .collect(Collectors.groupingBy(Item::getRequest));

        return reqs.stream().map(r -> {
            var dto = itemRequestMapper.toDto(r);
            var itemsForReq = groupReqId.getOrDefault(r.getId(), List.of());
            dto.setItems(itemsForReq.stream()
                    .map(it -> new RequestItemAnswerDto(
                            it.getId(),
                            it.getName(),
                            it.getOwner() != null ? it.getOwner().getId() : null))
                    .toList());
            return dto;
        }).toList();
    }


}
