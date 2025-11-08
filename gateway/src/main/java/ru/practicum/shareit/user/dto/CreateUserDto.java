package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CreateUserDto {

    @NotBlank(message = "Имя не может быть пустым")
    private String name;

    @NotBlank
    @Email(regexp = ".*@.*", message = "Электронная почта должна содержать символ '@'.")
    private String email;
}
