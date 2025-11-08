package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

    UserDto createUser(CreateUserDto userDto);

    UserDto update(Long id, UpdateUserDto userDto);

    UserDto findById(Long id);

    List<UserDto> findAll();

    void deleteById(Long id);

}