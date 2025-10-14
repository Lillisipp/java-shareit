package ru.practicum.shareit.request;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestCreateDto;

import java.util.List;

@Service
public interface ItemRequestService {
    public ItemRequestDto create(Long userId, RequestCreateDto dto);

    public ItemRequestDto getById(Long userId, Long requestId);

    public List<ItemRequestDto> getOwn(Long userId);

}
