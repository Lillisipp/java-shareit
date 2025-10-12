package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UpdateUserDto {

    private String name;

    @Email(regexp = ".*@.*", message = "Электронная почта должна содержать символ '@'.")
    private String email;
}
