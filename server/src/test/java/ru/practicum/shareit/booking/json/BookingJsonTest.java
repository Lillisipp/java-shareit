//package ru.practicum.shareit.booking.json;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.json.JsonTest;
//import ru.practicum.shareit.booking.dto.BookingCreateDto;
//
//import java.time.LocalDateTime;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@JsonTest
//class BookingJsonTest {
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void bookingCreateDto_Serialization_ShouldWork() throws Exception {
//        BookingCreateDto bookingDto = new BookingCreateDto(
//                1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1)
//        );
//
//        String json = objectMapper.writeValueAsString(bookingDto);
//
//        assertThat(json).contains("\"itemId\":1");
//        assertThat(json).contains("start");
//        assertThat(json).contains("end");
//    }
//
//    @Test
//    void bookingResponseDto_Deserialization_ShouldWork() throws Exception {
//        String json = "{\"id\":1,\"start\":\"2023-01-01T10:00:00\",\"end\":\"2023-01-02T10:00:00\",\"status\":\"WAITING\"}";
//
//        BookingResponseDto bookingDto = objectMapper.readValue(json, BookingResponseDto.class);
//
//        assertThat(bookingDto.getId()).isEqualTo(1L);
//        assertThat(bookingDto.getStatus()).isNotNull();
//    }
//}