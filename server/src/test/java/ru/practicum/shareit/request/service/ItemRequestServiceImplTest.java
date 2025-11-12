package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.GlobalExceptionHandler;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    ItemRequestRepository requestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestMapper itemRequestMapper;
    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemRequestServiceImpl service;


    @Test
    void create_userNotFound() {
        Long userId = 10L;
        RequestCreateDto in = new RequestCreateDto();
        in.setDescription("x");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(GlobalExceptionHandler.NotFoundException.class,
                () -> service.create(userId, in));

        verify(userRepository).findById(userId);
        verifyNoInteractions(itemRequestMapper);
        verifyNoInteractions(requestRepository);
    }


    @Test
    void getById_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class,
                () -> service.getById(1L, 5L));
        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(requestRepository, itemRequestMapper, itemRepository);
    }

    @Test
    void getById_requestNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(requestRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(GlobalExceptionHandler.NotFoundException.class,
                () -> service.getById(1L, 5L));

        verify(userRepository).findById(1L);
        verify(requestRepository).findById(5L);
        verifyNoInteractions(itemRequestMapper, itemRepository);
    }

    @Test
    void getOwn_ok() {
        Long userId = 7L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));

        ItemRequest r1 = new ItemRequest();
        r1.setId(1L);
        r1.setRequestor(userId);
        ItemRequest r2 = new ItemRequest();
        r2.setId(2L);
        r2.setRequestor(userId);

        List<ItemRequest> found = List.of(r1, r2);
        List<ItemRequestDto> mapped = List.of(
                dtoWithId(1L),
                dtoWithId(2L)
        );

        when(requestRepository.findByRequestor(userId)).thenReturn(found);
        when(itemRequestMapper.toDto(found)).thenReturn(mapped);

        List<ItemRequestDto> result = service.getOwn(userId);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(userRepository).findById(userId);
        verify(requestRepository).findByRequestor(userId);
        verify(itemRequestMapper).toDto(found);
        verifyNoMoreInteractions(userRepository, requestRepository, itemRequestMapper);
        verifyNoInteractions(itemRepository);
    }

    @Test
    void getOwn_userNotFound() {
        when(userRepository.findById(7L)).thenReturn(Optional.empty());
        assertThrows(GlobalExceptionHandler.NotFoundException.class,
                () -> service.getOwn(7L));
        verify(userRepository).findById(7L);
        verifyNoInteractions(requestRepository, itemRequestMapper, itemRepository);
    }

    @Test
    void getAllOthers_sizeLe0_returnsNull() {
        assertNull(service.getAllOthers(1L, 0, 0));
        assertNull(service.getAllOthers(1L, 10, -1));
        verifyNoInteractions(requestRepository, itemRepository, itemRequestMapper, userRepository);
    }

    @Test
    void getAllOthers_ok() {
        Long userId = 50L;
        int from = 20;
        int size = 10;

        ItemRequest r1 = new ItemRequest();
        r1.setId(100L);
        r1.setDescription("need A");
        r1.setRequestor(1L);
        ItemRequest r2 = new ItemRequest();
        r2.setId(200L);
        r2.setDescription("need B");
        r2.setRequestor(2L);

        var page = new PageImpl<>(List.of(r1, r2));
        when(requestRepository.findByRequestorNot(
                eq(userId),
                eq(PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")))
        )).thenReturn(page);

        Item i11 = new Item();
        i11.setId(11L);
        i11.setName("A1");
        i11.setRequest(100L);
        User o1 = new User();
        o1.setId(701L);
        i11.setOwner(o1);

        Item i12 = new Item();
        i12.setId(12L);
        i12.setName("A2");
        i12.setRequest(100L);
        User o2 = new User();
        o2.setId(702L);
        i12.setOwner(o2);

        Item i21 = new Item();
        i21.setId(21L);
        i21.setName("B1");
        i21.setRequest(200L);
        User o3 = new User();
        o3.setId(703L);
        i21.setOwner(o3);

        when(itemRepository.findByRequestIn(List.of(100L, 200L)))
                .thenReturn(List.of(i11, i12, i21));

        ItemRequestDto d1 = new ItemRequestDto();
        d1.setId(100L);
        d1.setDescription("need A");
        ItemRequestDto d2 = new ItemRequestDto();
        d2.setId(200L);
        d2.setDescription("need B");
        when(itemRequestMapper.toDto(r1)).thenReturn(d1);
        when(itemRequestMapper.toDto(r2)).thenReturn(d2);

        var result = service.getAllOthers(userId, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2, result.get(0).getItems().size());
        assertEquals(1, result.get(1).getItems().size());

        verify(requestRepository).findByRequestorNot(
                eq(userId),
                eq(PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")))
        );
        verify(itemRepository).findByRequestIn(List.of(100L, 200L));
        verify(itemRequestMapper).toDto(r1);
        verify(itemRequestMapper).toDto(r2);
        verifyNoMoreInteractions(requestRepository, itemRepository, itemRequestMapper);
        verifyNoInteractions(userRepository);
    }

    private static ItemRequestDto dtoWithId(Long id) {
        ItemRequestDto d = new ItemRequestDto();
        d.setId(id);
        return d;
    }
}
