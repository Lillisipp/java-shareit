package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

    public UserDto createUser(CreateUserDto userDto);

    public UserDto update(Long id, UpdateUserDto userDto);

    public UserDto findById(Long id);

    public List<UserDto> findAll();

    public void deleteById(Long id);

}