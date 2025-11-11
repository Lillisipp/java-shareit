package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestCreateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String USER = "X-Sharer-User-Id";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockBean
    RequestClient requestClient;

    @Test
    void create_ok_delegatesToClient() throws Exception {
        long userId = 1L;
        // заполнить валидно под @NotBlank/@Size в RequestCreateDto
        String body = """
                  {"description":"need drill"}
                """;

        when(requestClient.create(eq(userId), any(RequestCreateDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/requests")
                        .header(USER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(requestClient).create(eq(userId), any(RequestCreateDto.class));
    }

    @Test
    void create_missingHeader_returns400_andDoesNotCallClient() throws Exception {
        String body = """
                  {"description":"need drill"}
                """;

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void create_invalidBody_returns400_andDoesNotCallClient() throws Exception {
        // пустое тело или нарушенные @Valid поля
        mvc.perform(post("/requests")
                        .header(USER, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(requestClient);
    }

    @Test
    void getById_ok_delegatesToClient() throws Exception {
        long userId = 3L;
        long requestId = 100L;

        when(requestClient.getById(userId, requestId))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER, userId))
                .andExpect(status().isOk());

        verify(requestClient).getById(userId, requestId);
    }

    @Test
    void getOwn_ok_delegatesToClient() throws Exception {
        long userId = 4L;

        when(requestClient.getOwn(userId)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests")
                        .header(USER, userId))
                .andExpect(status().isOk());

        verify(requestClient).getOwn(userId);
    }

    @Test
    void getAllOthers_ok_withParams_andArgumentMapping() throws Exception {
        long userId = 5L;

        when(requestClient.getAllOthers(eq(userId), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/all")
                        .header(USER, userId)
                        .param("from", "10")
                        .param("size", "50"))
                .andExpect(status().isOk());

        ArgumentCaptor<Integer> fromCap = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> sizeCap = ArgumentCaptor.forClass(Integer.class);
        verify(requestClient).getAllOthers(eq(userId), fromCap.capture(), sizeCap.capture());
        assertEquals(10, fromCap.getValue());
        assertEquals(50, sizeCap.getValue());
    }

    @Test
    void getAllOthers_ok_usesDefaults_whenNoParams() throws Exception {
        long userId = 6L;

        when(requestClient.getAllOthers(eq(userId), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/requests/all")
                        .header(USER, userId))
                .andExpect(status().isOk());

        verify(requestClient).getAllOthers(userId, 0, 20); // дефолты контроллера
    }
}