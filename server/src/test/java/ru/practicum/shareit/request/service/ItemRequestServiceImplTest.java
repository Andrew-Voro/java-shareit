package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Test
    void addDescriptionNotNullValidBehavior() {

        Long userId = 0L;
        Long ownerId = 0L;
        Long requestId = 0L;
        Long itemId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        Item item = Item.builder().id(itemId).name("thing").description("thing").owner(owner).requestId(requestId)
                .available(true).build();
        List<Item> items = new ArrayList<>();
        items.add(item);
        LocalDateTime created = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        ItemRequest itemRequest = ItemRequest.builder().created(created).description("search thing")
                .items(items).requestor(user).id(requestId).build();
        ItemRequestDto itemRequestDto = RequestMapper.toRequestDto(itemRequest);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
        assertEquals(itemRequestDto, itemRequestService.add(userId, itemRequestDto));
        verify(itemRequestRepository).save(itemRequest);

    }

    @Test
    void addDescriptionNullUnValidBehavior() {
        Long userId = 0L;
        Long ownerId = 0L;
        Long requestId = 0L;
        Long itemId = 0L;
        String description = null;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        Item item = Item.builder().id(itemId).name("thing").description("thing").owner(owner).requestId(requestId)
                .available(true).build();
        List<Item> items = new ArrayList<>();
        items.add(item);
        LocalDateTime created = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        ItemRequest itemRequest = ItemRequest.builder().created(created).description(description)
                .items(items).requestor(user).id(requestId).build();
        ItemRequestDto itemRequestDto = RequestMapper.toRequestDto(itemRequest);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> itemRequestService.add(userId, itemRequestDto));
        verify(itemRequestRepository, never()).save(itemRequest);
    }


    @Test
    void getRequest() {
        Long userId = 0L;
        Long ownerId = 0L;
        Long requestId = 0L;
        Long itemId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        Item item = Item.builder().id(itemId).name("thing").description("thing").owner(owner).requestId(requestId)
                .available(true).build();
        List<Item> items = new ArrayList<>();
        items.add(item);
        LocalDateTime created = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        ItemRequest itemRequest = ItemRequest.builder().created(created).description("search thing")
                .items(items).requestor(user).id(requestId).build();
        ItemRequestDto itemRequestDto = RequestMapper.toRequestDto(itemRequest);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        assertEquals(itemRequestDto, itemRequestService.getRequest(userId, requestId));
        verify(itemRequestRepository).findById(requestId);

    }

    @Test
    void getOwn() {
        Long userId = 0L;
        Long ownerId = 0L;
        Long requestId = 0L;
        Long itemId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        Item item = Item.builder().id(itemId).name("thing").description("thing").owner(owner).requestId(requestId)
                .available(true).build();
        List<Item> items = new ArrayList<>();
        items.add(item);
        LocalDateTime created = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        ItemRequest itemRequest = ItemRequest.builder().created(created).description("search thing")
                .items(items).requestor(user).id(requestId).build();
        ItemRequestDto itemRequestDto = RequestMapper.toRequestDto(itemRequest);
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestor_idOrderByCreatedDesc(userId)).thenReturn(itemRequests);
        assertEquals(itemRequestDto, itemRequestService.getOwn(userId).get(0));
        verify(itemRequestRepository).findByRequestor_idOrderByCreatedDesc(userId);
    }

    @Test
    void getAllPaged() {
        Long userId = 0L;
        Long ownerId = 0L;
        Long requestId = 0L;
        Long itemId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        Item item = Item.builder().id(itemId).name("thing").description("thing").owner(owner).requestId(requestId)
                .available(true).build();
        List<Item> items = new ArrayList<>();
        items.add(item);
        LocalDateTime created = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        ItemRequest itemRequest = ItemRequest.builder().created(created).description("search thing")
                .items(items).requestor(user).id(requestId).build();
        ItemRequestDto itemRequestDto = RequestMapper.toRequestDto(itemRequest);
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
        Long from = 1L;
        Long size = 20L;
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        when(itemRequestRepository.findAllPaged(userId, page)).thenReturn(itemRequests);
        assertEquals(itemRequestDto, itemRequestService.getAllPaged(userId, from, size).get(0));
        verify(itemRequestRepository).findAllPaged(userId, page);
    }

    @Test
    void getAll() {
        Long userId = 0L;
        Long ownerId = 0L;
        Long requestId = 0L;
        Long itemId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        Item item = Item.builder().id(itemId).name("thing").description("thing").owner(owner).requestId(requestId)
                .available(true).build();
        List<Item> items = new ArrayList<>();
        items.add(item);
        LocalDateTime created = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        ItemRequest itemRequest = ItemRequest.builder().created(created).description("search thing")
                .items(items).requestor(user).id(requestId).build();
        ItemRequestDto itemRequestDto = RequestMapper.toRequestDto(itemRequest);
        List<ItemRequest> itemRequests = new ArrayList<>();
        itemRequests.add(itemRequest);
        when(itemRequestRepository.findAllIt(userId)).thenReturn(itemRequests);
        assertEquals(itemRequestDto, itemRequestService.getAll(userId).get(0));
        verify(itemRequestRepository).findAllIt(userId);

    }
}