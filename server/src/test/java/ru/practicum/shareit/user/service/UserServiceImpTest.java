package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImp service;

    // ---------- createUser ----------

    @Test
    void createUser_success() {
        CreateUserDto in = new CreateUserDto("Alice", "alice@mail.com");
        User mapped = new User(null, "Alice", "alice@mail.com");
        User saved = new User(10L, "Alice", "alice@mail.com");
        UserDto out = new UserDto(10L, "Alice", "alice@mail.com");

        when(userRepository.existsByEmail("alice@mail.com")).thenReturn(false);
        when(userRepository.save(mapped)).thenReturn(saved);

        try (MockedStatic<UserMapper> ms = mockStatic(UserMapper.class)) {
            ms.when(() -> UserMapper.fromCreate(in)).thenReturn(mapped);
            ms.when(() -> UserMapper.toDto(saved)).thenReturn(out);

            UserDto result = service.createUser(in);

            assertEquals(out, result);
            verify(userRepository).existsByEmail("alice@mail.com");
            verify(userRepository).save(mapped);
        }
    }

    @Test
    void createUser_blankEmail_throws() {
        CreateUserDto in = new CreateUserDto("Alice", " ");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.createUser(in));
        assertEquals("email required", ex.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    void createUser_blankName_throws() {
        CreateUserDto in = new CreateUserDto(" ", "a@b.c");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.createUser(in));
        assertEquals("name required", ex.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    void createUser_emailConflict_throws() {
        CreateUserDto in = new CreateUserDto("Alice", "a@b.c");
        when(userRepository.existsByEmail("a@b.c")).thenReturn(true);

        GlobalExceptionHandler.ConflictException ex =
                assertThrows(GlobalExceptionHandler.ConflictException.class, () -> service.createUser(in));

        assertEquals("email already used", ex.getMessage());
        verify(userRepository).existsByEmail("a@b.c");
        verify(userRepository, never()).save(any());
    }

    // ---------- update ----------

    @Test
    void update_userNotFound_throws() {
        when(userRepository.findById(77L)).thenReturn(Optional.empty());
        GlobalExceptionHandler.NotFoundException ex =
                assertThrows(GlobalExceptionHandler.NotFoundException.class,
                        () -> service.update(77L, new UpdateUserDto(null, null)));
        assertEquals("user not found", ex.getMessage());
        verify(userRepository).findById(77L);
    }

    @Test
    void update_emailConflict_throws() {
        User db = new User(5L, "Old", "old@mail.com");
        UpdateUserDto patch = new UpdateUserDto(null, "new@mail.com");

        when(userRepository.findById(5L)).thenReturn(Optional.of(db));
        when(userRepository.existsByEmailAndId("new@mail.com", 5L)).thenReturn(true);

        GlobalExceptionHandler.ConflictException ex =
                assertThrows(GlobalExceptionHandler.ConflictException.class, () -> service.update(5L, patch));

        assertEquals("email already used", ex.getMessage());
        verify(userRepository).findById(5L);
        verify(userRepository).existsByEmailAndId("new@mail.com", 5L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_success_usesMapperAndSaves() {
        User db = new User(5L, "Old", "old@mail.com");
        UpdateUserDto patch = new UpdateUserDto("NewName", "new@mail.com");
        User afterPatch = new User(5L, "NewName", "new@mail.com");
        User saved = new User(5L, "NewName", "new@mail.com");
        UserDto out = new UserDto(5L, "NewName", "new@mail.com");

        when(userRepository.findById(5L)).thenReturn(Optional.of(db));
        when(userRepository.existsByEmailAndId("new@mail.com", 5L)).thenReturn(false);
        when(userRepository.save(db)).thenReturn(saved);

        try (MockedStatic<UserMapper> ms = mockStatic(UserMapper.class)) {
            // Патчим сущность ссылочно
            ms.when(() -> UserMapper.patch(patch, db)).then(inv -> {
                db.setName(patch.getName());
                db.setEmail(patch.getEmail());
                return null;
            });
            ms.when(() -> UserMapper.toDto(saved)).thenReturn(out);

            UserDto result = service.update(5L, patch);

            assertEquals(out, result);
            // убеждаемся, что поля действительно изменены перед сохранением
            assertEquals("NewName", db.getName());
            assertEquals("new@mail.com", db.getEmail());

            verify(userRepository).findById(5L);
            verify(userRepository).existsByEmailAndId("new@mail.com", 5L);
            verify(userRepository).save(db);
        }
    }

    // ---------- findById ----------

    @Test
    void findById_found_returnsDto() {
        User db = new User(9L, "U", "u@mail.com");
        UserDto out = new UserDto(9L, "U", "u@mail.com");

        when(userRepository.findById(9L)).thenReturn(Optional.of(db));

        try (MockedStatic<UserMapper> ms = mockStatic(UserMapper.class)) {
            ms.when(() -> UserMapper.toDto(db)).thenReturn(out);
            UserDto result = service.findById(9L);
            assertEquals(out, result);
            verify(userRepository).findById(9L);
        }
    }

    @Test
    void findById_notFound_throws() {
        when(userRepository.findById(9L)).thenReturn(Optional.empty());
        GlobalExceptionHandler.NotFoundException ex =
                assertThrows(GlobalExceptionHandler.NotFoundException.class, () -> service.findById(9L));
        assertEquals("user not found", ex.getMessage());
        verify(userRepository).findById(9L);
    }

    // ---------- findAll ----------

    @Test
    void findAll_mapsEach() {
        User u1 = new User(1L, "A", "a@mail.com");
        User u2 = new User(2L, "B", "b@mail.com");
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        UserDto d1 = new UserDto(1L, "A", "a@mail.com");
        UserDto d2 = new UserDto(2L, "B", "b@mail.com");

        try (MockedStatic<UserMapper> ms = mockStatic(UserMapper.class)) {
            ms.when(() -> UserMapper.toDto(u1)).thenReturn(d1);
            ms.when(() -> UserMapper.toDto(u2)).thenReturn(d2);

            List<UserDto> result = service.findAll();
            assertEquals(List.of(d1, d2), result);
            verify(userRepository).findAll();
        }
    }

    // ---------- deleteById ----------

    @Test
    void deleteById_callsRepo() {
        service.deleteById(123L);
        verify(userRepository).deleteById(123L);
    }
}
