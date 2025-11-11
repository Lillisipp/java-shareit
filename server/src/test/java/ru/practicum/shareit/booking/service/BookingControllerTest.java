//package ru.practicum.shareit.booking.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import ru.practicum.shareit.booking.BookingController;
//import ru.practicum.shareit.booking.dto.BookingResponseDto;
//import ru.practicum.shareit.booking.model.BookingStatus;
//import ru.practicum.shareit.item.dto.BookingItemDto;
//import ru.practicum.shareit.user.dto.BookerDto;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(BookingController.class)
//class BookingControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private BookingService bookingService;
//
//    @Test
//    void getUserBookings_ShouldReturnBookings() throws Exception {
//        BookingResponseDto bookingDto = new BookingResponseDto(
//                1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
//                BookingStatus.WAITING, new BookerDto(1L, "Booker"),
//                new BookingItemDto(1L, "Item")
//        );
//
//        when(bookingService.getUserBookings(anyLong(), anyString(), anyInt(), anyInt()))
//                .thenReturn(List.of(bookingDto));
//
//        mockMvc.perform(get("/bookings")
//                        .header("X-Sharer-User-Id", "1")
//                        .param("state", "ALL")
//                        .param("from", "0")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1));
//    }
//
//    @Test
//    void getBooking_ShouldReturnBooking() throws Exception {
//        BookingResponseDto bookingDto = new BookingResponseDto(
//                1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
//                BookingStatus.WAITING, new BookerDto(1L, "Booker"),
//                new BookingItemDto(1L, "Item")
//        );
//
//        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDto);
//
//        mockMvc.perform(get("/bookings/1")
//                        .header("X-Sharer-User-Id", "1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1));
//    }
//}