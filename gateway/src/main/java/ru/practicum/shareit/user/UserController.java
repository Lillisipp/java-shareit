package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Valid @RequestBody CreateUserDto createUserDto) {
        return userClient.create(createUserDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id,
                                         @Valid @RequestBody UpdateUserDto userDto) {
        return userClient.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userClient.delete(id);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        return userClient.findById(id);
    }
}
