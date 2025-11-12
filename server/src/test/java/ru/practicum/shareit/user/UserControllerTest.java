package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean UserService userService;

    @Test
    void create_returns201_andBody() throws Exception {
        String body = om.writeValueAsString(Map.of("name", "Alice", "email", "alice@mail.com"));

        UserDto out = new UserDto();
        out.setId(1L);
        out.setName("Alice");
        out.setEmail("alice@mail.com");
        when(userService.createUser(any(CreateUserDto.class))).thenReturn(out);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Alice")))
                .andExpect(jsonPath("$.email", is("alice@mail.com")));

        verify(userService).createUser(any(CreateUserDto.class));
    }

    @Test
    void update_returns200_andBody() throws Exception {
        long id = 7L;
        String body = om.writeValueAsString(Map.of("name", "NewName", "email", "new@mail.com"));

        UserDto out = new UserDto();
        out.setId(id);
        out.setName("NewName");
        out.setEmail("new@mail.com");
        when(userService.update(eq(id), any(UpdateUserDto.class))).thenReturn(out);

        mvc.perform(patch("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(7)))
                .andExpect(jsonPath("$.name", is("NewName")))
                .andExpect(jsonPath("$.email", is("new@mail.com")));

        ArgumentCaptor<UpdateUserDto> captor = ArgumentCaptor.forClass(UpdateUserDto.class);
        verify(userService).update(eq(id), captor.capture());
    }

    @Test
    void delete_returns200_andDelegates() throws Exception {
        long id = 9L;

        mvc.perform(delete("/users/{id}", id))
                .andExpect(status().isOk());

        verify(userService).deleteById(id);
    }

    @Test
    void findAll_returns200_andList() throws Exception {
        UserDto u1 = new UserDto(); u1.setId(1L); u1.setName("A"); u1.setEmail("a@mail.com");
        UserDto u2 = new UserDto(); u2.setId(2L); u2.setName("B"); u2.setEmail("b@mail.com");
        when(userService.findAll()).thenReturn(List.of(u1, u2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(userService).findAll();
    }

    @Test
    void findById_returns200_andBody() throws Exception {
        long id = 3L;
        UserDto out = new UserDto();
        out.setId(id);
        out.setName("C");
        out.setEmail("c@mail.com");
        when(userService.findById(id)).thenReturn(out);

        mvc.perform(get("/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("C")))
                .andExpect(jsonPath("$.email", is("c@mail.com")));

        verify(userService).findById(id);
    }
}
