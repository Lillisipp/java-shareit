package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    @MockBean
    BookingClient bookingClient;

    @Test
    void createBooking_ok() throws Exception {
        long userId = 1L;
        BookingCreateDto dto = new BookingCreateDto();

        when(bookingClient.createBooking(eq(userId), any(BookingCreateDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(post("/bookings")
                        .header(USER_HEADER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(dto)))
                .andExpect(status().isOk());

        verify(bookingClient, times(1)).createBooking(eq(userId), any(BookingCreateDto.class));
    }

    @Test
    void approve_ok() throws Exception {
        long ownerId = 2L;
        long bookingId = 10L;

        when(bookingClient.approve(ownerId, bookingId, true))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USER_HEADER, ownerId)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingClient).approve(ownerId, bookingId, true);
    }

    @Test
    void getBookingById_ok() throws Exception {
        long userId = 3L;
        long bookingId = 11L;

        when(bookingClient.getBookingById(userId, bookingId))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USER_HEADER, userId))
                .andExpect(status().isOk());

        verify(bookingClient).getBookingById(userId, bookingId);
    }

    @Test
    void getBookings_mapsStateAndPaging_ok() throws Exception {
        long userId = 4L;

        ArgumentCaptor<BookingState> stateCap = ArgumentCaptor.forClass(BookingState.class);
        ArgumentCaptor<Integer> fromCap = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> sizeCap = ArgumentCaptor.forClass(Integer.class);

        when(bookingClient.getBookings(eq(userId), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings")
                        .header(USER_HEADER, userId)
                        .param("state", "current")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(bookingClient).getBookings(eq(userId), stateCap.capture(), fromCap.capture(), sizeCap.capture());
        // проверки маппинга
        assert stateCap.getValue() == BookingState.CURRENT;
        assert fromCap.getValue() == 0;
        assert sizeCap.getValue() == 5;
    }

    @Test
    void getOwnerBookings_mapsStateAndPaging_ok() throws Exception {
        long ownerId = 5L;

        ArgumentCaptor<BookingState> stateCap = ArgumentCaptor.forClass(BookingState.class);
        ArgumentCaptor<Integer> fromCap = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> sizeCap = ArgumentCaptor.forClass(Integer.class);

        when(bookingClient.getOwnerBookings(eq(ownerId), any(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, ownerId)
                        .param("state", "PAST")
                        .param("from", "2")
                        .param("size", "20"))
                .andExpect(status().isOk());

        verify(bookingClient).getOwnerBookings(eq(ownerId), stateCap.capture(), fromCap.capture(), sizeCap.capture());
        assert stateCap.getValue() == BookingState.PAST;
        assert fromCap.getValue() == 2;
        assert sizeCap.getValue() == 20;
    }


}