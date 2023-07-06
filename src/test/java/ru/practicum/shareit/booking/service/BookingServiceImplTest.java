package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoBack;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void addNewBookingUserNotOwnerValid() {

        Long userId = 0L;
        Long ownerId = 1L;
        Long bookingId = 0L;
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        User user = User.builder().name("an").email("an@com").id(userId).build();

        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId).comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);


        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        Booking actualBooking = bookingService.addNewBooking(bookingDto);
        assertEquals(booking, actualBooking);
        verify(bookingRepository).save(booking);
    }


    @Test
    void addNewBookingUserIsOwnerNotValid() {

        Long userId = 0L;
        Long ownerId = 1L;
        Long bookingId = 0L;

        User user = User.builder().name("an").email("an@com").id(userId).build();

        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId).comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, user);


        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        assertThrows(ObjectNotFoundException.class, () -> bookingService.addNewBooking(bookingDto));

        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void addNewBookingThingNotAvailable() {

        Long userId = 0L;
        Long ownerId = 1L;
        Long bookingId = 0L;
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        User user = User.builder().name("an").email("an@com").id(userId).build();

        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId).comments(new ArrayList<>()).available(false).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);

        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        assertThrows(ValidationException.class, () -> bookingService.addNewBooking(bookingDto));

        verify(bookingRepository, never()).save(booking);
    }


    @Test
    void getBookingOwnerOrCreatorValid() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();

        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId).comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, user);
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        assertEquals(BookingMapper.toBookingDtoBack(booking), bookingService.getBooking(bookingId, userId));
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void getBookingNotOwnerOrCreatorException() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long otherUser = 3L;
        Long userId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();

        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId).comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, user);
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        assertThrows(ObjectNotFoundException.class, () -> bookingService.getBooking(bookingId, otherUser));
    }

    @Test
    void getBookingOwnerWithOwner() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwner(ownerId)).thenReturn(bookings);
        assertEquals(bookings.stream().map(BookingMapper::toBookingDtoBack)
                .collect(Collectors.toList()), bookingService.getBookingOwner(ownerId));
        verify(bookingRepository).findByOwner(ownerId);
    }


    @Test
    void getBookingBooker() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByBooker(userId)).thenReturn(bookings);
        assertEquals(bookings.stream().map(BookingMapper::toBookingDtoBack)
                .collect(Collectors.toList()), bookingService.getBookingBooker(userId));
        verify(bookingRepository).findByBooker(userId);
    }

    @Test
    void getAllOwnBookingPaged() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        List<Booking> bookings = new ArrayList<>();

        bookings.add(booking);
        Long from = 1L;
        Long size = 20L;
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findByOwnerPaged(userId, page)).thenReturn(bookings);

        assertEquals(BookingMapper.toBookingDtoBack(booking), bookingService.getAllOwnBookingPaged(userId, from, size).get(0));
        verify(bookingRepository).findByOwnerPaged(userId, page);

    }

    @Test
    void getAllBookingPaged() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().minusDays(1);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        List<Booking> bookings = new ArrayList<>();


        bookings.add(booking);
        Long from = 1L;
        Long size = 20L;

        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        when(bookingRepository.findByBookerPaged(userId, page)).thenReturn(bookings);

        assertEquals(BookingMapper.toBookingDtoBack(booking), bookingService.getAllBookingPaged(userId, from, size).get(0));
        verify(bookingRepository).findByBookerPaged(userId, page);

    }

    @Test
    void getBookingBookerFuture() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(bookingRepository.findByBooker_IdAndStartIsAfterOrderByIdDesc(userId, now)).thenReturn(bookings);
        assertEquals(BookingMapper.toBookingDtoBack(booking), bookingService.getBookingBookerFuture(userId, now).get(0));
        verify(bookingRepository).findByBooker_IdAndStartIsAfterOrderByIdDesc(userId, now);

    }

    @Test
    void getBookingBookerPast() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(bookingRepository.findByBooker_IdAndEndIsBeforeOrderByIdDesc(userId, now)).thenReturn(bookings);
        assertEquals(BookingMapper.toBookingDtoBack(booking), bookingService.getBookingBookerPast(userId, now).get(0));
        verify(bookingRepository).findByBooker_IdAndEndIsBeforeOrderByIdDesc(userId, now);
    }

    @Test
    void getBookingBookerCurrent() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(bookingRepository.findByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByIdAsc(userId, now, now)).thenReturn(bookings);
        assertEquals(BookingMapper.toBookingDtoBack(booking), bookingService.getBookingBookerCurrent(userId, now).get(0));
        verify(bookingRepository).findByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByIdAsc(userId, now, now);
    }

    @Test
    void getBookingOwnerFuture() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(bookingRepository.findByOwnerAndStartIsAfter(ownerId, now)).thenReturn(bookings);
        assertEquals(BookingMapper.toBookingDtoBack(booking), bookingService.getBookingOwnerFuture(ownerId, now).get(0));
        verify(bookingRepository).findByOwnerAndStartIsAfter(ownerId, now);
    }

    @Test
    void getBookingOwnerPast() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(bookingRepository.findByOwnerAndEndIsBefore(ownerId, now)).thenReturn(bookings);
        assertEquals(BookingMapper.toBookingDtoBack(booking), bookingService.getBookingOwnerPast(ownerId, now).get(0));
        verify(bookingRepository).findByOwnerAndEndIsBefore(ownerId, now);
    }

    @Test
    void getBookingOwnerCurrent() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(bookingRepository.findByOwner_IdAndEndIsAfterAndStartIsBeforeOrderByIdAsc(ownerId, now)).thenReturn(bookings);
        assertEquals(BookingMapper.toBookingDtoBack(booking), bookingService.getBookingOwnerCurrent(ownerId, now).get(0));
        verify(bookingRepository).findByOwner_IdAndEndIsAfterAndStartIsBeforeOrderByIdAsc(ownerId, now);
    }

    @Test
    void bookingUpdateByOwnerValidBehavior() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        Status status = Status.WAITING;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);

        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(status).start(start).end(end).id(bookingId).build();
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        BookingDtoBack actualBookingDtoBack = bookingService.bookingUpdate(bookingId, ownerId, status);
        assertEquals(BookingMapper.toBookingDtoBack(booking), actualBookingDtoBack);
        verify(bookingRepository).save(booking);
    }

    @Test
    void bookingUpdateByUserUnValidBehavior() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        Status status = Status.WAITING;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);

        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(status).start(start).end(end).id(bookingId).build();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        assertThrows(ObjectNotFoundException.class, () -> bookingService.bookingUpdate(bookingId, userId, status));
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void bookingUpdateByOwnerStatusApprovedWithoutUpdate() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        Status status = Status.APPROVED;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);

        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(status).start(start).end(end).id(bookingId).build();
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        assertThrows(ValidationException.class, () -> bookingService.bookingUpdate(bookingId, ownerId, status));
        verify(bookingRepository, never()).save(booking);
    }


    @Test
    void getBookingOwnerStatus() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        Status status = Status.WAITING;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);

        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(status).start(start).end(end).id(bookingId).build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByOwnerAndStatus(ownerId, status)).thenReturn(bookings);
        assertEquals(BookingMapper.toBookingDtoBack(booking), bookingService.getBookingOwnerStatus(ownerId, status).get(0));
        verify(bookingRepository).findByOwnerAndStatus(ownerId, status);

    }

    @Test
    void getBookingBookerStatus() {
        Long bookingId = 0L;
        Long ownerId = 1L;
        Long userId = 0L;
        Status status = Status.WAITING;
        User user = User.builder().name("an").email("an@com").id(userId).build();
        User owner = User.builder().name("anl").email("anl@com").id(ownerId).build();
        ItemDto itemDto = ItemDto.builder().name("thing").description("thing").owner(ownerId)
                .comments(new ArrayList<>()).available(true).build();
        Item item = ItemMapper.toDtoItem(itemDto, owner);

        LocalDateTime start = LocalDateTime.now().minusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Booking booking = Booking.builder().item(item).booker(user)
                .status(status).start(start).end(end).id(bookingId).build();
        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBooker_IdAndStatus(userId, status)).thenReturn(bookings);
        assertEquals(BookingMapper.toBookingDtoBack(booking), bookingService.getBookingBookerStatus(userId, status).get(0));
        verify(bookingRepository).findByBooker_IdAndStatus(userId, status);
    }
}