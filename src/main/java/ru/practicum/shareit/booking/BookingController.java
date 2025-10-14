package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

/**
 * TODO Sprint add-bookings.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(USER_HEADER) Long userId,
                                    @Valid @PathVariable BookingCreateDto dto) {
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(USER_HEADER) Long ownerId,
                              @PathVariable Long bookingId,
                              @RequestParam boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@RequestHeader(USER_HEADER) Long userId,
                          @PathVariable Long bookingId) {
        return bookingService.findById(userId, bookingId);
    }

}
