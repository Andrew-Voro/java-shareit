package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    ItemRepository repository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void getItems() {
        Long userId = 0L;
        Long itemId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(userId).comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, user);
        List<Item> items = new ArrayList<>();
        items.add(item);
        when(repository.findByOwnerOrderById(userId)).thenReturn(items);
        List<ItemDto> actualItemsDto = itemService.getItems(userId);
        assertEquals(ItemMapper.toDtoItem(actualItemsDto.get(0), user), items.get(0));
        verify(repository).findByOwnerOrderById(userId);
    }


    @Test
    void addNewItemWithoutUser() {
        Long userId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(userId).comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, user);
        when(userRepository.findById(userId)).thenThrow(new ObjectNotFoundException("User not found"));
        assertThrows(ObjectNotFoundException.class, () -> itemService.addNewItem(userId, itemDto));
        verify(repository, never()).save(item);
    }

    @Test
    void addNewItem() {
        Long userId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(userId).comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, user);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.save(item)).thenReturn(item);

        ItemDto actualItemDto = itemService.addNewItem(userId, itemDto);
        assertEquals(itemDto, actualItemDto);
        verify(repository).save(item);
    }

    @Test
    void getItemNotFoundItemException() {
        Long commentId = 0L;
        Long itemId = 0L;
        Long userId = 0L;

        User user = User.builder().name("an").email("an@com").id(userId).build();

        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(userId).comments(new ArrayList<>()).available(true).build();
        Comment comment = Comment.builder().id(commentId).author(user).created(LocalDateTime.now()).text("new").build();
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        Item item = ItemMapper.toDtoItem(itemDto, user);
        List<CommentDto> commentsDto = comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());

        itemDto.setComments(commentsDto);

        when(repository.findById(itemId)).thenThrow(new ObjectNotFoundException("Item not found"));
        assertThrows(ObjectNotFoundException.class, () -> itemService.getItem(userId, userId));

    }


    @Test
    void getItem() {
        Long commentId = 0L;
        Long itemId = 0L;
        Long userId = 0L;

        User user = User.builder().name("an").email("an@com").id(userId).build();

        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(userId).comments(new ArrayList<>()).available(true).build();
        Comment comment = Comment.builder().id(commentId).author(user).created(LocalDateTime.now()).text("new").build();
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);

        Item item = ItemMapper.toDtoItem(itemDto, user);
        List<CommentDto> commentsDto = comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());

        itemDto.setComments(commentsDto);
        when(commentRepository.findByItem_idOrderById(itemId)).thenReturn(comments);
        when(repository.findById(itemId)).thenReturn(Optional.of(item));
        ItemDto actualItemDto = itemService.getItem(userId, userId);
        assertEquals(itemDto, actualItemDto);
        verify(repository).findById(itemId);

    }

    @Test
    void updateItemNotFoundItem() {
        Long userId = 0L;
        UserDto userDto = new UserDto();
        userDto.setEmail("a@n.com");
        userDto.setId(userId);
        Long itemId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", "1");
        fields.put("name", "thing");
        fields.put("description", "new thing");
        fields.put("available", false);
        ItemDto itemDtoUpdate = ItemDto.builder().available(true).owner(userId).id(1L)
                .description("new thing").name("thing").build();
        List<Item> items = new ArrayList<>();
        when(repository.findByOwnerOrderById(userId).stream().filter(x -> x.getId().equals(itemId))
                .collect(Collectors.toList()).size() == 0).thenThrow(new ObjectNotFoundException("Item not found"));
        assertThrows(ObjectNotFoundException.class, () -> itemService.updateItem(fields, userId, itemId));

    }

    @Test
    void searchByNameOrDescription() {
        Long userId = 0L;
        String text = "thing";
        User user = User.builder().name("an").email("an@com").id(userId).build();
        ItemDto itemDto = ItemDto.builder().name(text).description(text).owner(userId).comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, user);
        List<Item> items = new ArrayList<>();
        items.add(item);
        when(repository.searchByNameOrDescription(text)).thenReturn(items);
        List<ItemDto> actualItemsDto = itemService.searchByNameOrDescription(text);
        assertEquals(ItemMapper.toDtoItem(actualItemsDto.get(0), user), items.get(0));
        verify(repository).searchByNameOrDescription(text);
    }

    @Test
    void addComment() {
        Long commentId = 0L;
        Long itemId = 0L;
        Long userId = 0L;
        Long bookingId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        LocalDateTime created = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));

        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(userId).comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, user);
        Comment comment = Comment.builder().id(commentId).author(user).item(item).created(created).text("new").build();

        CommentDto commentDto = CommentMapper.toCommentDto(comment);


        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(repository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_IdAndBooker_idAndStatus(itemId, userId, Status.APPROVED)).thenReturn(Optional.of(bookings));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto actualAddComment = itemService.addComment(itemId, userId, commentDto);
        assertEquals(actualAddComment, commentDto);
        verify(commentRepository).save(comment);

    }
}