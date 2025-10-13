package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final RequestRepository repo;
    private final UserRepository userRepo;
    private final ItemRequestMapper mapper;

    @Override
    public ItemRequestDto create(Long userId, RequestCreateDto dto) {
        User requestor = userRepo.findById(userId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("user not found"));
        ItemRequest entity = mapper.toEntity(dto, requestor);
        return mapper.toDto(repo.save(entity));
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("user not found"));
        ItemRequest r = repo.findById(requestId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("request not found"));
        return mapper.toDto(r);
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("user not found"));
        return mapper.toDto(repo.findByRequestorId(userId));
    }

}
