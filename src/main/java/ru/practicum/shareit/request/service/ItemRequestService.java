package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestCreateDto;

import java.util.List;

@Service
public interface ItemRequestService {
    ItemRequestDto create(Long userId, RequestCreateDto dto);

    ItemRequestDto getById(Long userId, Long requestId);

    List<ItemRequestDto> getOwn(Long userId);

    List<ItemRequestDto> getAllOthers(Long userId, int from, int size);
}
