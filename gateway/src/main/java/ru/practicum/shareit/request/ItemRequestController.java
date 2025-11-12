package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestCreateDto;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestClient requestClient;
    private static final String USER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER) Long userId,
                                         @Valid @RequestBody RequestCreateDto dto) {
        return requestClient.create(userId, dto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER) Long userId,
                                          @PathVariable Long requestId) {
        return requestClient.getById(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwn(@RequestHeader(USER) Long userId) {
        return requestClient.getOwn(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOthers(@RequestHeader(USER) Long userId,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "20") int size) {
        return requestClient.getAllOthers(userId, from, size);
    }
}
