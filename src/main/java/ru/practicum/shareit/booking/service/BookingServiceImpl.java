package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.Role;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final BookingMapper mapper;

    @Override
    public BookingDto create(Long userId, BookingCreateDto dto) {
        if (!dto.getStart().isBefore(dto.getEnd())) {
            throw new IllegalArgumentException("start must be before end");
        }
        if (dto.getStart().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("start must be in future");
        }
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
        if (bookingRepo.existsByItem_IdAndStatusAndStartLessThanAndEndGreaterThan(item.getId(), BookingStatus.APPROVED, dto.getStart(), dto.getEnd()))
            throw new IllegalStateException("overlaps with approved booking");

        Booking b = mapper.toEntity(dto, item, booker);


        b = bookingRepo.save(b);
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
            if (bookingRepo.existsByItem_IdAndStatusAndStartLessThanAndEndGreaterThan(b.getItem().getId(), BookingStatus.APPROVED, b.getStart(), b.getEnd()))
                throw new IllegalStateException("overlaps with approved booking");
            b.setStatus(BookingStatus.APPROVED);
        } else {
            b.setStatus(BookingStatus.REJECTED);
        }
        return mapper.toDto(bookingRepo.save(b));//или криэйт
    }

    @Override
    public BookingDto getStatusById(Long userId, Long bookingId) {
        Booking b = bookingRepo.findDetailedById(bookingId)
                .orElseThrow(() -> new GlobalExceptionHandler.NotFoundException("Бронирование не найдено"));
        boolean isOwner = b.getItem() != null && b.getItem().getOwner() != null
                && b.getItem().getOwner().getId().equals(userId);
        boolean isBooker = b.getBooker() != null && b.getBooker().getId().equals(userId);
        if (!isOwner && !isBooker)
            throw new IllegalStateException("Нет доступа к этому бронированию");
        return mapper.toDto(b);
    }

    @Override
    public List<BookingDto> getBookings(Long userId, Role role, BookingState state, int from, int size) {
        if (!userRepo.existsById(userId)) throw new GlobalExceptionHandler.NotFoundException("user not found");
        var now = LocalDateTime.now();

        Pageable pg = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, "start"));

        Page<Booking> page = switch (role) {
            case BOOKER -> switch (state) {
                case ALL -> bookingRepo.findByBooker_Id(userId, pg);
                case CURRENT -> bookingRepo.findByBooker_IdAndStartBeforeAndEndAfter(userId, now, now, pg);
                case PAST -> bookingRepo.findByBooker_IdAndEndBefore(userId, now, pg);
                case FUTURE -> bookingRepo.findByBooker_IdAndStartAfter(userId, now, pg);
                case WAITING -> bookingRepo.findByBooker_IdAndStatus(userId, BookingStatus.WAITING, pg);
                case REJECTED -> bookingRepo.findByBooker_IdAndStatus(userId, BookingStatus.REJECTED, pg);
            };
            case OWNER -> switch (state) {
                case ALL -> bookingRepo.findByItem_Owner_Id(userId, pg);
                case CURRENT -> bookingRepo.findByItem_Owner_IdAndStartBeforeAndEndAfter(userId, now, now, pg);
                case PAST -> bookingRepo.findByItem_Owner_IdAndEndBefore(userId, now, pg);
                case FUTURE -> bookingRepo.findByItem_Owner_IdAndStartAfter(userId, now, pg);
                case WAITING -> bookingRepo.findByItem_Owner_IdAndStatus(userId, BookingStatus.WAITING, pg);
                case REJECTED -> bookingRepo.findByItem_Owner_IdAndStatus(userId, BookingStatus.REJECTED, pg);
            };
        };

        return page
                .stream()
                .map(mapper::toDto)
                .toList();
    }


}
