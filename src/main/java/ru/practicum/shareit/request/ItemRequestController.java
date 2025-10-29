package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestCreateDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestServiceImpl service;
    private static final String USER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER) Long userId,
                                 @Valid @RequestBody RequestCreateDto dto) {
        return service.create(userId, dto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto get(@RequestHeader(USER) Long userId,
                              @PathVariable Long requestId) {
        return service.getById(userId, requestId);
    }

    @GetMapping
    public List<ItemRequestDto> own(@RequestHeader(USER) Long userId) {
        return service.getOwn(userId);
    }


}
