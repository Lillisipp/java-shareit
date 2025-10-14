package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

@UtilityClass
public class UserMapper {
    public static UserDto toDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static User fromCreate(CreateUserDto in) {
        User u = new User();
        u.setName(in.getName());
        u.setEmail(in.getEmail());
        return u;
    }

    public static User toModel(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }

    public void patch(UpdateUserDto in, User target) {
        if (in.getName() != null && !in.getName().isBlank()) target.setName(in.getName());
        if (in.getEmail() != null && !in.getEmail().isBlank()) target.setEmail(in.getEmail());
    }
}
