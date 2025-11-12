package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    BookingRepository bookingRepo;
    @Mock
    ItemRepository itemRepo;
    @Mock
    UserRepository userRepo;
    @Mock
    BookingMapper mapper;

    @InjectMocks
    BookingServiceImpl service;

    private User booker;
    private User owner;
    private Item item;
    private BookingCreateDto createDto;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void init() {
        owner = new User();
        owner.setId(10L);
        booker = new User();
        booker.setId(20L);

        item = new Item();
        item.setId(100L);
        item.setOwner(owner);
        item.setAvailable(true);

        createDto = new BookingCreateDto();
        createDto.setItemId(item.getId());
        createDto.setStart(LocalDateTime.now().plusHours(1));
        createDto.setEnd(LocalDateTime.now().plusHours(2));

        booking = new Booking();
        booking.setId(500L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(createDto.getStart());
        booking.setEnd(createDto.getEnd());
        booking.setStatus(BookingStatus.WAITING);

        bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStatus(booking.getStatus());
    }

    // -------- create()

    @Test
    void create_startNotBeforeEnd_throws() {
        createDto.setStart(createDto.getEnd());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.create(booker.getId(), createDto));
        assertEquals("start must be before end", ex.getMessage());
        verifyNoInteractions(userRepo, itemRepo, bookingRepo, mapper);
    }

    @Test
    void create_bookerNotFound_throws() {
        when(userRepo.findById(booker.getId())).thenReturn(Optional.empty());

        GlobalExceptionHandler.NotFoundException ex = assertThrows(
                GlobalExceptionHandler.NotFoundException.class,
                () -> service.create(booker.getId(), createDto));
        assertEquals("booker not found", ex.getMessage());
        verify(userRepo).findById(booker.getId());
        verifyNoMoreInteractions(userRepo);
        verifyNoInteractions(itemRepo, bookingRepo, mapper);
    }

    @Test
    void create_itemNotFound_throws() {
        when(userRepo.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepo.findById(item.getId())).thenReturn(Optional.empty());

        GlobalExceptionHandler.NotFoundException ex = assertThrows(
                GlobalExceptionHandler.NotFoundException.class,
                () -> service.create(booker.getId(), createDto));
        assertEquals("item not found", ex.getMessage());
        verify(userRepo).findById(booker.getId());
        verify(itemRepo).findById(item.getId());
        verifyNoInteractions(bookingRepo, mapper);
    }

    @Test
    void create_itemOwnerMissing_throws() {
        item.setOwner(null);
        when(userRepo.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepo.findById(item.getId())).thenReturn(Optional.of(item));

        GlobalExceptionHandler.NotFoundException ex = assertThrows(
                GlobalExceptionHandler.NotFoundException.class,
                () -> service.create(booker.getId(), createDto));
        assertEquals("item owner missing", ex.getMessage());
        verifyNoInteractions(bookingRepo, mapper);
    }

    @Test
    void create_ownerBooksOwnItem_throws() {
        when(userRepo.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepo.findById(item.getId())).thenReturn(Optional.of(item));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.create(owner.getId(), createDto));
        assertEquals("owner cannot book own item", ex.getMessage());
        verifyNoInteractions(bookingRepo, mapper);
    }

    @Test
    void create_itemNotAvailable_throws() {
        item.setAvailable(false);
        when(userRepo.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepo.findById(item.getId())).thenReturn(Optional.of(item));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.create(booker.getId(), createDto));
        assertEquals("item not available", ex.getMessage());
        verifyNoInteractions(bookingRepo, mapper);
    }

    @Test
    void create_overlapsApproved_throws() {
        when(userRepo.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepo.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepo.existsByItem_IdAndStatusAndStartLessThanAndEndGreaterThan(
                item.getId(), BookingStatus.APPROVED, createDto.getStart(), createDto.getEnd()))
                .thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.create(booker.getId(), createDto));
        assertEquals("overlaps with approved booking", ex.getMessage());
        verifyNoInteractions(mapper);
    }

    @Test
    void create_success_mapsAndSaves() {
        when(userRepo.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepo.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepo.existsByItem_IdAndStatusAndStartLessThanAndEndGreaterThan(
                anyLong(), any(), any(), any())).thenReturn(false);
        when(mapper.toEntity(eq(createDto), eq(item), eq(booker))).thenReturn(booking);
        when(bookingRepo.save(booking)).thenReturn(booking);
        when(mapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto res = service.create(booker.getId(), createDto);

        assertNotNull(res);
        assertEquals(bookingDto.getId(), res.getId());
        verify(mapper).toEntity(createDto, item, booker);
        verify(bookingRepo).save(booking);
        verify(mapper).toDto(booking);
    }

    // -------- approve()

    @Test
    void approve_notFound_throws() {
        when(bookingRepo.findById(booking.getId())).thenReturn(Optional.empty());

        GlobalExceptionHandler.NotFoundException ex = assertThrows(
                GlobalExceptionHandler.NotFoundException.class,
                () -> service.approve(owner.getId(), booking.getId(), true));
        assertEquals("booking not found", ex.getMessage());
    }


    @Test
    void approve_alreadyDecided_throws() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepo.findById(booking.getId())).thenReturn(Optional.of(booking));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.approve(owner.getId(), booking.getId(), true));
        assertEquals("already decided", ex.getMessage());
    }

    @Test
    void approve_true_withOverlap_throws() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepo.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepo.existsByItem_IdAndStatusAndStartLessThanAndEndGreaterThan(
                item.getId(), BookingStatus.APPROVED, booking.getStart(), booking.getEnd()))
                .thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.approve(owner.getId(), booking.getId(), true));
        assertEquals("overlaps with approved booking", ex.getMessage());
    }

    @Test
    void approve_true_success_setsApproved_andSaves() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepo.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepo.existsByItem_IdAndStatusAndStartLessThanAndEndGreaterThan(anyLong(), any(), any(), any()))
                .thenReturn(false);
        when(bookingRepo.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto res = service.approve(owner.getId(), booking.getId(), true);

        assertEquals(bookingDto.getId(), res.getId());
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        verify(bookingRepo).save(booking);
        verify(mapper).toDto(booking);
    }

    @Test
    void approve_false_success_setsRejected_andSaves() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepo.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepo.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto res = service.approve(owner.getId(), booking.getId(), false);

        assertEquals(BookingStatus.REJECTED, booking.getStatus());
        assertEquals(bookingDto.getId(), res.getId());
        verify(bookingRepo).save(booking);
        verify(mapper).toDto(booking);
    }

    // -------- getStatusById()

    @Test
    void getStatusById_notFound_throws() {
        when(bookingRepo.findDetailedById(booking.getId())).thenReturn(Optional.empty());

        GlobalExceptionHandler.NotFoundException ex = assertThrows(
                GlobalExceptionHandler.NotFoundException.class,
                () -> service.getStatusById(booker.getId(), booking.getId()));
        assertEquals("Бронирование не найдено", ex.getMessage());
    }

    @Test
    void getStatusById_accessDenied_throws() {
        when(bookingRepo.findDetailedById(booking.getId())).thenReturn(Optional.of(booking));
        // userId не владелец и не бронирующий
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.getStatusById(999L, booking.getId()));
        assertEquals("Нет доступа к этому бронированию", ex.getMessage());
    }

    @Test
    void getStatusById_ok_forOwner_orBooker() {
        when(bookingRepo.findDetailedById(booking.getId())).thenReturn(Optional.of(booking));
        when(mapper.toDto(booking)).thenReturn(bookingDto);

        // как владелец
        BookingDto res1 = service.getStatusById(owner.getId(), booking.getId());
        assertEquals(bookingDto.getId(), res1.getId());

        // как бронирующий
        BookingDto res2 = service.getStatusById(booker.getId(), booking.getId());
        assertEquals(bookingDto.getId(), res2.getId());

        verify(mapper, times(2)).toDto(booking);
    }

    // -------- getBookings()

    @Test
    void getBookings_userNotExists_throws() {
        when(userRepo.existsById(123L)).thenReturn(false);

        GlobalExceptionHandler.NotFoundException ex = assertThrows(
                GlobalExceptionHandler.NotFoundException.class,
                () -> service.getBookings(123L, Role.BOOKER, BookingState.ALL, 0, 10));
        assertEquals("user not found", ex.getMessage());
        verifyNoInteractions(bookingRepo, mapper);
    }

    @Nested
    class GetBookingsVariants {

        @BeforeEach
        void mockCommon() {
            when(userRepo.existsById(anyLong())).thenReturn(true);
            when(mapper.toDto(any(Booking.class))).thenReturn(bookingDto);
        }

        @Test
        void booker_all_current_past_future_statuses() {
            // ALL
            when(bookingRepo.findByBooker_Id(eq(booker.getId()), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            // CURRENT
            when(bookingRepo.findByBooker_IdAndStartBeforeAndEndAfter(eq(booker.getId()), any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            // PAST
            when(bookingRepo.findByBooker_IdAndEndBefore(eq(booker.getId()), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            // FUTURE
            when(bookingRepo.findByBooker_IdAndStartAfter(eq(booker.getId()), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            // WAITING
            when(bookingRepo.findByBooker_IdAndStatus(eq(booker.getId()), eq(BookingStatus.WAITING), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            // REJECTED
            when(bookingRepo.findByBooker_IdAndStatus(eq(booker.getId()), eq(BookingStatus.REJECTED), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));

            ArgumentCaptor<Pageable> cap = ArgumentCaptor.forClass(Pageable.class);
            List<BookingDto> all = service.getBookings(booker.getId(), Role.BOOKER, BookingState.ALL, 20, 10);
            assertEquals(1, all.size());
            verify(bookingRepo).findByBooker_Id(eq(booker.getId()), cap.capture());
            Pageable pg = cap.getValue();
            assertEquals(2, pg.getPageNumber()); // from/size -> 20/10 => page=2
            assertEquals(10, pg.getPageSize());
            assertEquals(Sort.Direction.DESC, pg.getSort().getOrderFor("start").getDirection());

            // Остальные состояния возвращают 1 запись и проходят через mapper
            assertEquals(1, service.getBookings(booker.getId(), Role.BOOKER, BookingState.CURRENT, 0, 10).size());
            assertEquals(1, service.getBookings(booker.getId(), Role.BOOKER, BookingState.PAST, 0, 10).size());
            assertEquals(1, service.getBookings(booker.getId(), Role.BOOKER, BookingState.FUTURE, 0, 10).size());
            assertEquals(1, service.getBookings(booker.getId(), Role.BOOKER, BookingState.WAITING, 0, 10).size());
            assertEquals(1, service.getBookings(booker.getId(), Role.BOOKER, BookingState.REJECTED, 0, 10).size());
        }

        @Test
        void owner_all_current_past_future_statuses() {
            // ALL
            when(bookingRepo.findByItem_Owner_Id(eq(owner.getId()), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            // CURRENT
            when(bookingRepo.findByItem_Owner_IdAndStartBeforeAndEndAfter(eq(owner.getId()), any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            // PAST
            when(bookingRepo.findByItem_Owner_IdAndEndBefore(eq(owner.getId()), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            // FUTURE
            when(bookingRepo.findByItem_Owner_IdAndStartAfter(eq(owner.getId()), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            // WAITING
            when(bookingRepo.findByItem_Owner_IdAndStatus(eq(owner.getId()), eq(BookingStatus.WAITING), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));
            // REJECTED
            when(bookingRepo.findByItem_Owner_IdAndStatus(eq(owner.getId()), eq(BookingStatus.REJECTED), any()))
                    .thenReturn(new PageImpl<>(List.of(booking)));

            ArgumentCaptor<Pageable> cap = ArgumentCaptor.forClass(Pageable.class);
            List<BookingDto> all = service.getBookings(owner.getId(), Role.OWNER, BookingState.ALL, 0, 20);
            assertEquals(1, all.size());
            verify(bookingRepo).findByItem_Owner_Id(eq(owner.getId()), cap.capture());
            Pageable pg = cap.getValue();
            assertEquals(0, pg.getPageNumber());
            assertEquals(20, pg.getPageSize());
            assertEquals(Sort.Direction.DESC, pg.getSort().getOrderFor("start").getDirection());

            // Остальные состояния
            assertEquals(1, service.getBookings(owner.getId(), Role.OWNER, BookingState.CURRENT, 0, 20).size());
            assertEquals(1, service.getBookings(owner.getId(), Role.OWNER, BookingState.PAST, 0, 20).size());
            assertEquals(1, service.getBookings(owner.getId(), Role.OWNER, BookingState.FUTURE, 0, 20).size());
            assertEquals(1, service.getBookings(owner.getId(), Role.OWNER, BookingState.WAITING, 0, 20).size());
            assertEquals(1, service.getBookings(owner.getId(), Role.OWNER, BookingState.REJECTED, 0, 20).size());
        }
    }
}
