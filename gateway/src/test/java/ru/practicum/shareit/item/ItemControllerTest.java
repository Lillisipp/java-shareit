package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockBean
    ItemClient itemClient;

    @Test
    void create_ok_delegatesToClient() throws Exception {
        long userId = 1L;
        String body = """
                  {"name":"Drill","description":"Powerful","available":true}
                """;

        when(itemClient.create(eq(userId), any()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/items")
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(itemClient).create(eq(userId), any());
    }

    @Test
    void update_ok_delegatesToClient() throws Exception {
        long userId = 2L;
        long itemId = 10L;
        String body = """
                  {"name":"New name","description":"Updated"}
                """;

        when(itemClient.update(eq(userId), eq(itemId), any()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(itemClient).update(eq(userId), eq(itemId), any());
    }

    @Test
    void getById_ok_delegatesToClient() throws Exception {
        long userId = 3L;
        long itemId = 11L;

        when(itemClient.getById(userId, itemId)).thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(itemClient).getById(userId, itemId);
    }


    @Test
    void search_ok_delegatesToClient() throws Exception {
        long requesterId = 5L;

        when(itemClient.search(eq(requesterId), eq("drill")))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items/search")
                        .header(USER_HEADER, requesterId)
                        .param("text", "drill"))
                .andExpect(status().isOk());

        verify(itemClient).search(requesterId, "drill");
    }

    @Test
    void addComment_ok_delegatesToClient() throws Exception {
        long userId = 6L;
        long itemId = 13L;
        String body = """
                  {"text":"Nice tool"}
                """;

        when(itemClient.addComment(eq(userId), eq(itemId), any()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        verify(itemClient).addComment(eq(userId), eq(itemId), any());
    }

    @Test
    void findAllItemsByUser_ok_withParams() throws Exception {
        long ownerId = 7L;

        when(itemClient.findAllItemsByUser(eq(ownerId), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items")
                        .header(USER_HEADER, ownerId)
                        .param("from", "10")
                        .param("size", "50"))
                .andExpect(status().isOk());

        ArgumentCaptor<Integer> fromCap = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> sizeCap = ArgumentCaptor.forClass(Integer.class);
        verify(itemClient).findAllItemsByUser(eq(ownerId), fromCap.capture(), sizeCap.capture());
        assertEquals(10, fromCap.getValue());
        assertEquals(50, sizeCap.getValue());
    }

    @Test
    void findAllItemsByUser_ok_usesDefaults() throws Exception {
        long ownerId = 8L;

        when(itemClient.findAllItemsByUser(eq(ownerId), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/items")
                        .header(USER_HEADER, ownerId))
                .andExpect(status().isOk());

        verify(itemClient).findAllItemsByUser(ownerId, 0, 20);
    }

    @Test
    void create_missingHeader_400() throws Exception {
        String body = """
                  {"name":"Drill","description":"Powerful","available":true}
                """;

        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }


}