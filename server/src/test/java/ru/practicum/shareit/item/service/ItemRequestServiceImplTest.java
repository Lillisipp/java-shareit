package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepository commentRepo;
    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImp itemService;

    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("o@mail.com");

        item = new Item();
        item.setId(10L);
        item.setName("Drill");
        item.setDescription("Power");
        item.setAvailable(true);
        item.setOwner(owner);
    }

    // ---------- create ----------

    @Test
    void create_WhenOwnerNotFound_ThrowsNotFound() {
        var dto = new ItemCreateDto("Drill", "Power", true, null);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(GlobalExceptionHandler.NotFoundException.class,
                () -> itemService.create(1L, dto));

        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(itemMapper, itemRepository);
    }

    @Test
    void create_WhenRequestIdProvidedButMissing_ThrowsNotFound() {
        var dto = new ItemCreateDto("Drill", "Power", true, 777L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemMapper.toItemFromCreateDto(dto, owner)).thenReturn(item);
        when(requestRepository.existsById(777L)).thenReturn(false);

        assertThrows(GlobalExceptionHandler.NotFoundException.class,
                () -> itemService.create(1L, dto));

        verify(requestRepository).existsById(777L);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void create_Ok_SavesAndReturnsDto() {
        var dto = new ItemCreateDto("Drill", "Power", true, null);
        var saved = new Item();
        saved.setId(10L);

        var dtoOut = new ItemDto();
        dtoOut.setId(10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(itemMapper.toItemFromCreateDto(dto, owner)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(saved);
        when(itemMapper.toItemDto(saved)).thenReturn(dtoOut);

        var result = itemService.create(1L, dto);

        assertEquals(10L, result.getId());
        verify(itemRepository).save(item);
    }

    // ---------- update ----------

    @Test
    void update_WhenItemMissing_ThrowsNotFound() {
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class,
                () -> itemService.update(1L, 10L, new ItemUpdateDto()));
    }

    @Test
    void update_WhenCallerNotOwner_ThrowsNotFound() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        assertThrows(GlobalExceptionHandler.NotFoundException.class,
                () -> itemService.update(999L, 10L, new ItemUpdateDto()));
        verify(itemRepository).findById(10L);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void update_Ok_MapsAndSaves() {
        var upd = new ItemUpdateDto();
        var saved = new Item();
        saved.setId(10L);
        var dtoOut = new ItemDto();
        dtoOut.setId(10L);

        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        doAnswer(inv -> null).when(itemMapper).updateItemFromUpdateDto(upd, item);
        when(itemRepository.save(item)).thenReturn(saved);
        when(itemMapper.toItemDto(saved)).thenReturn(dtoOut);

        var result = itemService.update(1L, 10L, upd);

        assertEquals(10L, result.getId());
        verify(itemMapper).updateItemFromUpdateDto(upd, item);
    }

    // ---------- getById ----------

    @Test
    void getById_WhenMissing_ThrowsNotFound() {
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class,
                () -> itemService.getById(2L, 10L));
    }

    @Test
    void getById_Ok_ReturnsDtoWithCommentsAndNullBookings() {
        var dtoOut = new ItemDto();
        dtoOut.setId(10L);
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));

        var comments = List.of(new Comment());
        var commentsDto = List.of(new CommentDto());
        when(commentRepo.findByItem_IdOrderByCreatedAsc(10L)).thenReturn(comments);
        when(commentMapper.toDto(comments)).thenReturn(commentsDto);
        when(itemMapper.toItemDto(item)).thenReturn(dtoOut);

        var result = itemService.getById(2L, 10L);

        assertEquals(10L, result.getId());
        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
        assertEquals(1, result.getComments().size());
    }

    // ---------- search ----------

    @Test
    void search_WhenBlank_ReturnsEmpty() {
        var res = itemService.search(2L, "   ");
        assertTrue(res.isEmpty());
        verifyNoInteractions(itemRepository);
    }

    @Test
    void search_Ok_ReturnsMappedList() {
        var found = List.of(item);
        var dto = new ItemDto();
        dto.setId(10L);
        when(itemRepository.search("dr")).thenReturn(found);
        when(itemMapper.toItemDto(item)).thenReturn(dto);

        var res = itemService.search(2L, "dr");

        assertEquals(1, res.size());
        assertEquals(10L, res.getFirst().getId());
    }

    // ---------- delete ----------

    @Test
    void delete_WhenNotOwner_ThrowsIllegalState() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        assertThrows(IllegalStateException.class, () -> itemService.delete(999L, 10L));
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void delete_Ok_CallsRepositoryDelete() {
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        itemService.delete(1L, 10L);
        verify(itemRepository).deleteById(10L);
    }

    // ---------- findAllByOwnerWithBookings ----------

    @Test
    void findAllByOwnerWithBookings_WhenSizeZero_ReturnsEmpty() {
        assertTrue(itemService.findAllByOwnerWithBookings(1L, 0, 0).isEmpty());
        verifyNoInteractions(itemRepository, bookingRepository, commentRepo);
    }

    @Test
    void findAllByOwnerWithBookings_WhenPageEmpty_ReturnsEmpty() {
        when(itemRepository.findByOwner_Id(eq(1L), any())).thenReturn(Page.empty());
        assertTrue(itemService.findAllByOwnerWithBookings(1L, 0, 20).isEmpty());
    }


    @Test
    void addComment_WhenEmptyText_ThrowsIllegalState() {
        var ex = assertThrows(IllegalStateException.class,
                () -> itemService.addComment(2L, 10L, new CommentCreateDto("   ")));
        assertEquals("комментарий пуст", ex.getMessage());
    }

    @Test
    void addComment_WhenUserMissing_ThrowsNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class,
                () -> itemService.addComment(2L, 10L, new CommentCreateDto("ok")));
    }

    @Test
    void addComment_WhenItemMissing_ThrowsNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class,
                () -> itemService.addComment(2L, 10L, new CommentCreateDto("ok")));
    }

    @Test
    void addComment_WhenNotAllowed_ThrowsIllegalState() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                eq(2L), eq(10L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)
        )).thenReturn(false);

        var ex = assertThrows(IllegalStateException.class,
                () -> itemService.addComment(2L, 10L, new CommentCreateDto("ok")));
        assertEquals("пользователь не брал товар", ex.getMessage());
    }

    @Test
    void addComment_Ok_SavesAndReturnsDto() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(10L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBooker_IdAndItem_IdAndStatusAndEndBefore(
                eq(2L), eq(10L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)
        )).thenReturn(true);

        var saved = new Comment();
        var dto = new CommentDto();
        when(commentRepo.save(any(Comment.class))).thenReturn(saved);
        when(commentMapper.toDto(saved)).thenReturn(dto);

        var out = itemService.addComment(2L, 10L, new CommentCreateDto("ok"));
        assertNotNull(out);
        verify(commentRepo).save(any(Comment.class));
    }
}
