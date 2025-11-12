package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockBean
    ItemService itemService;

    @Test
    void create_ok_returnsBody_andDelegates() throws Exception {
        long userId = 1L;
        String body = om.writeValueAsString(
                Map.of("name", "Drill", "description", "Powerful", "available", true)
        );

        ItemDto out = new ItemDto();
        out.setId(100L);
        out.setName("Drill");
        out.setDescription("Powerful");
        out.setAvailable(true);

        when(itemService.create(eq(userId), any(ItemCreateDto.class))).thenReturn(out);

        mvc.perform(post("/items")
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().json(om.writeValueAsString(out)));

        verify(itemService).create(eq(userId), any(ItemCreateDto.class));
    }

    @Test
    void create_missingHeader_returns400() throws Exception {
        String body = om.writeValueAsString(
                Map.of("name", "Drill", "description", "Powerful", "available", true)
        );

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

    @Test
    void update_ok_returnsBody_andDelegates() throws Exception {
        long userId = 2L;
        long itemId = 10L;
        String body = om.writeValueAsString(
                Map.of("name", "New name", "description", "Updated")
        );

        ItemDto out = new ItemDto();
        out.setId(itemId);
        out.setName("New name");
        out.setDescription("Updated");
        out.setAvailable(true);

        when(itemService.update(eq(userId), eq(itemId), any(ItemUpdateDto.class))).thenReturn(out);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().json(om.writeValueAsString(out)));

        verify(itemService).update(eq(userId), eq(itemId), any(ItemUpdateDto.class));
    }

    @Test
    void getById_ok_returnsBody_andDelegates() throws Exception {
        long userId = 3L;
        long itemId = 11L;

        ItemDto out = new ItemDto();
        out.setId(itemId);
        out.setName("Saw");
        out.setDescription("Sharp");
        out.setAvailable(true);

        when(itemService.getById(userId, itemId)).thenReturn(out);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(content().json(om.writeValueAsString(out)));

        verify(itemService).getById(userId, itemId);
    }

    @Test
    void delete_ok_delegates() throws Exception {
        long ownerId = 4L;
        long itemId = 12L;

        mvc.perform(delete("/items/{itemId}", itemId)
                        .header(USER_HEADER, ownerId))
                .andExpect(status().isOk());

        verify(itemService).delete(ownerId, itemId);
    }

    @Test
    void search_ok_returnsList_andDelegates() throws Exception {
        long requesterId = 5L;

        ItemDto i1 = new ItemDto();
        i1.setId(1L);
        i1.setName("Drill");
        i1.setDescription("Power");
        i1.setAvailable(true);
        ItemDto i2 = new ItemDto();
        i2.setId(2L);
        i2.setName("Adapter");
        i2.setDescription("Drill adapter");
        i2.setAvailable(true);
        when(itemService.search(requesterId, "drill")).thenReturn(List.of(i1, i2));

        mvc.perform(get("/items/search")
                        .header(USER_HEADER, requesterId)
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(itemService).search(requesterId, "drill");
    }

    @Test
    void addComment_ok_returnsBody_andDelegates() throws Exception {
        long userId = 6L;
        long itemId = 13L;
        String body = om.writeValueAsString(Map.of("text", "Nice tool"));

        CommentDto out = new CommentDto();
        out.setId(200L);
        out.setText("Nice tool");
        out.setAuthorName("User6");
        out.setCreated(LocalDateTime.now());

        when(itemService.addComment(eq(userId), eq(itemId), any(CommentCreateDto.class))).thenReturn(out);

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().json(om.writeValueAsString(out)));

        verify(itemService).addComment(eq(userId), eq(itemId), any(CommentCreateDto.class));
    }

    @Test
    void findAllItemsByUser_ok_withParams_andDelegates() throws Exception {
        long ownerId = 7L;

        ItemOwnerDto o1 = new ItemOwnerDto();
        o1.setId(1L);
        o1.setName("Drill");
        ItemOwnerDto o2 = new ItemOwnerDto();
        o2.setId(2L);
        o2.setName("Saw");

        when(itemService.findAllByOwnerWithBookings(eq(ownerId), anyInt(), anyInt()))
                .thenReturn(List.of(o1, o2));

        mvc.perform(get("/items")
                        .header(USER_HEADER, ownerId)
                        .param("from", "10")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        ArgumentCaptor<Integer> fromCap = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> sizeCap = ArgumentCaptor.forClass(Integer.class);
        verify(itemService).findAllByOwnerWithBookings(eq(ownerId), fromCap.capture(), sizeCap.capture());
        org.junit.jupiter.api.Assertions.assertEquals(10, fromCap.getValue());
        org.junit.jupiter.api.Assertions.assertEquals(50, sizeCap.getValue());
    }

    @Test
    void findAllItemsByUser_ok_usesDefaults() throws Exception {
        long ownerId = 8L;

        when(itemService.findAllByOwnerWithBookings(eq(ownerId), anyInt(), anyInt()))
                .thenReturn(List.of());

        mvc.perform(get("/items")
                        .header(USER_HEADER, ownerId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemService).findAllByOwnerWithBookings(ownerId, 0, 20);
    }
}
