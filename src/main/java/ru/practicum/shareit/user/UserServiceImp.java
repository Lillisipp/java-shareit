package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto createUser(CreateUserDto userDto) {
        if (userDto.getEmail() == null || userDto.getEmail().isBlank())
            throw new IllegalArgumentException("email required");
        if (userDto.getName() == null || userDto.getName().isBlank())
            throw new IllegalArgumentException("name required");
        if (repository.existsByEmail(userDto.getEmail()))
            throw new GlobalExceptionHandler.ConflictException("email already used");

        User saved = repository.save(UserMapper.fromCreate(userDto));
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto update(Long id, UpdateUserDto userDto) {
        User user = repository.findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("user not found"));
        if (userDto.getEmail() != null
                && repository.existsByEmailOrNot(userDto.getEmail(), id))
            throw new GlobalExceptionHandler.ConflictException("email already used");
        UserMapper.patch(userDto, user);
        User update = repository.save(user);
        return UserMapper.toDto(update);
    }

    @Override
    public UserDto findById(Long id) {
        return UserMapper.toDto(repository.findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("user not found")));
    }

    @Override
    public List<UserDto> findAll() {
        return repository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
