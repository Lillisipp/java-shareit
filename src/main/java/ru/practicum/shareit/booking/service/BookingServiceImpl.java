package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final BookingMapper mapper;

    @Override
    public BookingDto create(Long userId, BookingCreateDto dto) {
        if (!dto.getStart().isBefore(dto.getEnd()))
            throw new IllegalArgumentException("start must be before end");
        if (dto.getStart().isBefore(LocalDateTime.now()))
            throw new IllegalArgumentException("start must be in future");

        User booker = userRepo.findById(userId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("booker not found"));
        Item item = itemRepo.findById(dto.getItemId())
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("item not found"));

        if (item.getOwner() == null)
            throw new GlobalExceptionHandler.NotFoundException("item owner missing");
        if (item.getOwner().getId().equals(userId))
            throw new IllegalStateException("owner cannot book own item");
        if (!Boolean.TRUE.equals(item.getAvailable()))
            throw new IllegalStateException("item not available");
        if (bookingRepo.hasApprovedOverlap(item.getId(), dto.getStart(), dto.getEnd()))
            throw new IllegalStateException("overlaps with approved booking");

        Booking b = mapper.toEntity(dto, item, booker);
        b = bookingRepo.create(b);
        return mapper.toDto(b);
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking b = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("booking not found"));

        if (b.getItem() == null || b.getItem().getOwner() == null
                || !b.getItem().getOwner().getId().equals(ownerId))
            throw new IllegalStateException("only owner can approve");
        if (b.getStatus() != BookingStatus.WAITING)
            throw new IllegalStateException("already decided");

        if (approved) {
            if (bookingRepo.hasApprovedOverlap(b.getItem().getId(), b.getStart(), b.getEnd()))
                throw new IllegalStateException("overlaps with approved booking");
            b.setStatus(BookingStatus.APPROVED);
        } else {
            b.setStatus(BookingStatus.REJECTED);
        }
        return mapper.toDto(bookingRepo.create(b));
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        Booking b = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("booking not found"));
        boolean isOwner = b.getItem() != null && b.getItem().getOwner() != null
                && b.getItem().getOwner().getId().equals(userId);
        boolean isBooker = b.getBooker() != null && b.getBooker().getId().equals(userId);
        if (!isOwner && !isBooker)
            throw new IllegalStateException("only owner or booker can view");
        return mapper.toDto(b);
    }
}
