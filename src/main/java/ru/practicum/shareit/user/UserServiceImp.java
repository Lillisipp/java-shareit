package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(CreateUserDto userDto) {
        log.debug("createUser: payload={}", userDto);
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            log.warn("createUser: email is blank");
            throw new IllegalArgumentException("email required");
        }
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            log.warn("createUser: name is blank");
            throw new IllegalArgumentException("name required");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.info("createUser: email already used email={}", userDto.getEmail());
            throw new GlobalExceptionHandler.ConflictException("email already used");
        }
        User saved = userRepository.save(UserMapper.fromCreate(userDto));
        log.info("createUser: created id={}", saved.getId());
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto update(Long id, UpdateUserDto userDto) {
        log.debug("update: id={}, payload={}", id, userDto);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.info("update: user not found id={}", id);
                    return new GlobalExceptionHandler.NotFoundException("user not found");
                });

        if (userDto.getEmail() != null && userRepository.existsByEmailAndId(userDto.getEmail(), id)) {
            log.info("update: email already used id={}, email={}", id, userDto.getEmail());
            throw new GlobalExceptionHandler.ConflictException("email already used");
        }

        UserMapper.patch(userDto, user);
        User update = userRepository.save(user);
        log.info("update: updated id={}", update.getId());
        return UserMapper.toDto(update);
    }

    @Override
    public UserDto findById(Long id) {
        return UserMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("user not found")));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        log.debug("deleteById: id={}", id);
        userRepository.deleteById(id);
        log.info("deleteById: deleted id={}", id);
    }
}
