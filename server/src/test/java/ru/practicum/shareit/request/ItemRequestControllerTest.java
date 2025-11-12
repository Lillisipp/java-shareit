package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String USER = "X-Sharer-User-Id";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockBean
    ItemRequestServiceImpl service;

    @Test
    void create_ok_delegatesAndReturnsBody() throws Exception {
        long userId = 1L;
        String body = om.writeValueAsString(Map.of("description", "need drill"));

        ItemRequestDto out = new ItemRequestDto();
        // предполагаем базовые геттеры в DTO
        out.setId(100L);
        out.setDescription("need drill");
        out.setCreated(LocalDateTime.now());

        when(service.create(eq(userId), any(RequestCreateDto.class))).thenReturn(out);

        mvc.perform(post("/requests")
                        .header(USER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().json(om.writeValueAsString(out)));

        ArgumentCaptor<RequestCreateDto> captor = ArgumentCaptor.forClass(RequestCreateDto.class);
        verify(service).create(eq(userId), captor.capture());
    }

    @Test
    void create_missingHeader_returns400_andNoCall() throws Exception {
        String body = om.writeValueAsString(Map.of("description", "need drill"));

        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void getById_ok_delegatesAndReturnsBody() throws Exception {
        long userId = 2L;
        long requestId = 77L;

        ItemRequestDto out = new ItemRequestDto();
        out.setId(requestId);
        out.setDescription("need saw");
        out.setCreated(LocalDateTime.now());

        when(service.getById(userId, requestId)).thenReturn(out);

        mvc.perform(get("/requests/{requestId}", requestId)
                        .header(USER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(om.writeValueAsString(out)));

        verify(service).getById(userId, requestId);
    }

    @Test
    void getOwn_ok_delegatesAndReturnsList() throws Exception {
        long userId = 3L;
        ItemRequestDto r1 = new ItemRequestDto();
        r1.setId(1L);
        r1.setDescription("d1");
        r1.setCreated(LocalDateTime.now());
        ItemRequestDto r2 = new ItemRequestDto();
        r2.setId(2L);
        r2.setDescription("d2");
        r2.setCreated(LocalDateTime.now());

        when(service.getOwn(userId)).thenReturn(List.of(r1, r2));

        mvc.perform(get("/requests").header(USER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(om.writeValueAsString(List.of(r1, r2))));

        verify(service).getOwn(userId);
    }

    @Test
    void getAllOthers_ok_withParams_andArgumentMapping() throws Exception {
        long userId = 4L;
        ItemRequestDto r = new ItemRequestDto();
        r.setId(3L);
        r.setDescription("x");
        r.setCreated(LocalDateTime.now());
        when(service.getAllOthers(eq(userId), anyInt(), anyInt())).thenReturn(List.of(r));

        mvc.perform(get("/requests/all")
                        .header(USER, userId)
                        .param("from", "10")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(content().json(om.writeValueAsString(List.of(r))));

        ArgumentCaptor<Integer> fromCap = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> sizeCap = ArgumentCaptor.forClass(Integer.class);
        verify(service).getAllOthers(eq(userId), fromCap.capture(), sizeCap.capture());
        assertEquals(10, fromCap.getValue());
        assertEquals(50, sizeCap.getValue());
    }

    @Test
    void getAllOthers_ok_usesDefaults_whenNoParams() throws Exception {
        long userId = 5L;
        when(service.getAllOthers(eq(userId), anyInt(), anyInt())).thenReturn(List.of());

        mvc.perform(get("/requests/all").header(USER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(service).getAllOthers(userId, 0, 20);
    }
}
