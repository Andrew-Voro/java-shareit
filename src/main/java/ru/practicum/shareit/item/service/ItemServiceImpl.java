package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> getItems(Long owner) {
        List<ItemDto> userItemsDto = ItemMapper.toItemDto(repository.findByOwnerOrderById(owner));
        for (ItemDto itemDto : userItemsDto) {
            setLastNextBookingWithoutStatus(itemDto, itemDto.getId());
        }
        return userItemsDto;
    }

    @Transactional
    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Item item = repository.save(ItemMapper.toDtoItem(itemDto, user));
        return ItemMapper.toItemDto(item);
    }

    @Transactional(readOnly = true)
    @Override//
    public ItemDto getItem(Long itemId, Long userId) {
        ItemDto itemDto = ItemMapper.toItemDto(repository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Item not found")));
        List<Comment> comments = commentRepository.findByItem_idOrderById(itemId);
        if (!comments.isEmpty()) {

            itemDto.setComments(comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        }
        try {

            if (itemDto.getOwner().equals(userId)) {

                setLastNextBooking(itemDto, itemId);
            }
        } catch (Exception e) {
            return itemDto;
        }
        return itemDto;
    }

    @Transactional
    @Override
    public ItemDto updateItem(Map<String, Object> fields, Long userId, Long itemId) {

        Item item = repository.findByOwnerOrderById(userId).stream().filter(x -> x.getId().equals(itemId)).collect(Collectors.toList()).get(0);
        fields.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(Item.class, k);
            field.setAccessible(true);
            if (v instanceof Integer) {
                Long w = ((Integer) v).longValue();
                ReflectionUtils.setField(field, item, w);
            } else {
                ReflectionUtils.setField(field, item, v);
            }
        });
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));//
        item.setOwner(user);
        repository.save(item);
        return ItemMapper.toItemDto(item);
    }

    private ItemDto setLastNextBooking(ItemDto itemDto, Long itemId) {
        Booking last;
        Booking next;
        try {
            last = bookingRepository.findByItem_Id(itemId).stream().filter(x -> x.getStatus().equals(Status.APPROVED)).filter(x -> x.getStart().isBefore(LocalDateTime.now())).reduce((a, b) ->
                    a.getStart().isAfter(b.getStart()) ? a : b).get();
            itemDto.setLastBooking(BookingMapper.toBookingDtoForItem(last));
        } catch (Exception e) {
            itemDto.setLastBooking(null);
        }
        try {
            next = bookingRepository.findByItem_Id(itemId).stream().filter(x -> x.getStatus().equals(Status.APPROVED)).filter(x -> x.getStart().isAfter(LocalDateTime.now())).reduce((a, b) ->
                    a.getStart().isBefore(b.getStart()) ? a : b).get();
            itemDto.setNextBooking(BookingMapper.toBookingDtoForItem(next));
        } catch (Exception e) {
            itemDto.setNextBooking(null);
            return itemDto;
        }


        return itemDto;
    }

    private ItemDto setLastNextBookingWithoutStatus(ItemDto itemDto, Long itemId) {
        Booking last;
        Booking next;
        try {
            last = bookingRepository.findByItem_Id(itemId).stream().filter(x -> x.getStart().isBefore(LocalDateTime.now())).reduce((a, b) ->
                    a.getStart().isAfter(b.getStart()) ? a : b).get();
            itemDto.setLastBooking(BookingMapper.toBookingDtoForItem(last));
        } catch (Exception e) {
            itemDto.setLastBooking(null);

        }
        try {
            next = bookingRepository.findByItem_Id(itemId).stream().filter(x -> x.getStart().isAfter(LocalDateTime.now())).reduce((a, b) ->
                    a.getStart().isBefore(b.getStart()) ? a : b).get();
            itemDto.setNextBooking(BookingMapper.toBookingDtoForItem(next));
        } catch (Exception e) {
            itemDto.setNextBooking(null);
            return itemDto;
        }
        return itemDto;
    }

    @Transactional(readOnly = true)
    public List<ItemDto> searchByNameOrDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        text = text.toLowerCase();
        return ItemMapper.toItemDto(repository.searchByNameOrDescription(text));
    }

    @Transactional
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        Item item = repository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Item not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        List<Booking> bookings = bookingRepository.findByItem_IdAndBooker_idAndStatus(itemId, userId, Status.APPROVED)
                .orElseThrow(() -> new ValidationException("Booking not found"));
        List<Booking> bookingsNotFuture = bookings.stream().filter(x -> x.getStart().isBefore(LocalDateTime.now())).collect(Collectors.toList());
        if (bookingsNotFuture.isEmpty()) {
            throw new ValidationException("Not found current or past Booking ");
        }
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentRepository.save(CommentMapper.toDtoComment(commentDto, user, item));
        return CommentMapper.toCommentDto(comment);
    }
}
