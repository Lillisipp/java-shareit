package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserClient userClient;

    @Test
    void create_returns201_andDelegatesToClient() throws Exception {
        CreateUserDto dto = new CreateUserDto();
        // если у тебя в CreateUserDto аннотации @NotBlank/@Email — заполним поля
        // замените на сеттеры или билдер, как у вас принято
        // пример через рефлексию Jackson: просто сериализуем карту
        String body = """
                  {"name":"Alice","email":"alice@mail.com"}
                """;

        when(userClient.create(any(CreateUserDto.class)))
                .thenReturn(ResponseEntity.status(201).build());

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());

        verify(userClient, times(1)).create(any(CreateUserDto.class));
    }

    @Test
    void update_returns200_andDelegatesToClient() throws Exception {
        long id = 7L;
        String body = """
                  {"name":"NewName","email":"new@mail.com"}
                """;

        when(userClient.update(eq(id), any(UpdateUserDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        ArgumentCaptor<UpdateUserDto> captor = ArgumentCaptor.forClass(UpdateUserDto.class);
        verify(userClient).update(eq(id), captor.capture());

    }


    @Test
    void findAll_returns200_andDelegatesToClient() throws Exception {
        when(userClient.findAll()).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).findAll();
    }

    @Test
    void findById_returns200_andDelegatesToClient() throws Exception {
        long id = 3L;
        when(userClient.findById(id)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk());

        verify(userClient).findById(id);
    }
}