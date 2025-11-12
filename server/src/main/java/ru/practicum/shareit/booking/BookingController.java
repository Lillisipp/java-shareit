package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.Role;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(USER_HEADER) Long userId,
                                    @RequestBody BookingCreateDto dto) {
        return bookingService.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader(USER_HEADER) Long ownerId,
                              @PathVariable Long bookingId,
                              @RequestParam boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader(USER_HEADER) Long userId,
                              @PathVariable Long bookingId) {
        return bookingService.getStatusById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingsUser(
            @RequestHeader(USER_HEADER) Long userId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        return bookingService.getBookings(userId, Role.BOOKER, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsOwner(
            @RequestHeader(USER_HEADER) Long ownerId,
            @RequestParam(required = false, defaultValue = "ALL") BookingState state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "20") int size
    ) {
        return bookingService.getBookings(ownerId, Role.OWNER, state, from, size);
    }
}
