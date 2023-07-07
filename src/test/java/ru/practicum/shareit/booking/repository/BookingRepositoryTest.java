package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    private void addBookings() {

        User owner = userRepository.save(User.builder().email("a@n.com").name("an").build());
        User user = userRepository.save(User.builder().email("ai@n.com").name("ain").build());
        LocalDateTime startPast = now().minusDays(2);
        LocalDateTime endPast = now().minusDays(1);
        LocalDateTime startFuture = now().plusDays(1);
        LocalDateTime endFuture = now().plusDays(2);
        ItemRequest requestOne = itemRequestRepository.save(ItemRequest.builder().created(now()).description("thing").items(new ArrayList<>())
                .requestor(user).build());
        ItemRequest requestTwo = itemRequestRepository.save(ItemRequest.builder().created(now()).description("thing2").items(new ArrayList<>())
                .requestor(user).build());
        ItemRequest requestThree = itemRequestRepository.save(ItemRequest.builder().created(now()).description("thing3").items(new ArrayList<>())
                .requestor(user).build());

        Item itemOne = itemRepository.save(Item.builder().requestId(requestOne.getId()).available(true).description("thing").name("thing")
                .owner(owner).build());
        Item itemTwo = itemRepository.save(Item.builder().requestId(requestTwo.getId()).available(true).description("thing2").name("thing2")
                .owner(owner).build());
        Item itemThree = itemRepository.save(Item.builder().requestId(requestThree.getId()).available(true).description("thing3").name("thing3")
                .owner(owner).build());
        Booking bookingOne = bookingRepository.save(Booking.builder().booker(user).item(itemOne)
                .status(Status.WAITING).end(endPast).start(startPast).build());
        Booking bookingTwo = bookingRepository.save(Booking.builder().booker(user).item(itemTwo)
                .status(Status.WAITING).end(endFuture).start(startPast).build());
        Booking bookingThree = bookingRepository.save(Booking.builder().booker(user).item(itemThree)
                .status(Status.WAITING).end(endFuture).start(startFuture).build());
    }

    @Test
    void findByOwner() {

        List<Booking> bookings = bookingRepository.findByOwner(1L);
        assertEquals(bookings.size(), 3);//0
        assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);

    }

    @Test
    void findByOwnerPaged() {

        Long from = 1L;
        Long size = 2L;
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        List<Booking> bookings = bookingRepository.findByOwnerPaged(1L, page);
        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);


    }

    @Test
    void findByBooker() {
        List<Booking> bookings = bookingRepository.findByBooker(2L);

        assertEquals(bookings.size(), 3);//0
        assertEquals(bookingRepository.findAll().size(), 3);
        assertEquals(bookings.get(0).getBooker().getId(), 1L);
    }

    @Test
    void findByBookerPaged() {
        Long from = 1L;
        Long size = 2L;
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        List<Booking> bookings = bookingRepository.findByBookerPaged(2L, page);
        assertEquals(bookings.size(), 2);
        assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);
    }

    @Test
    void findByBooker_IdAndStartIsAfterOrderByIdDesc() {
        List<Booking> bookings = bookingRepository.findByBooker_IdAndStartIsAfterOrderByIdDesc(2L, now());
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker().getId(), 2L);
    }


    @Test
    void findByBooker_IdAndEndIsBeforeOrderByIdDesc() {
        List<Booking> bookings = bookingRepository.findByBooker_IdAndEndIsBeforeOrderByIdDesc(2L, now());
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker().getId(), 2L);
    }


    @Test
    void findByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByIdAsc() {
        LocalDateTime now = now();
        List<Booking> bookings = bookingRepository.findByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByIdAsc(2L,
                now, now);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker().getId(), 2L);
    }

    @Test
    void findByOwnerAndStartIsAfter() {
        List<Booking> bookings = bookingRepository.findByOwnerAndStartIsAfter(1L, now());
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);
    }

    @Test
    void findByOwnerAndEndIsBefore() {
        List<Booking> bookings = bookingRepository.findByOwnerAndEndIsBefore(1L, now());
        assertEquals(bookings.size(), 1);//1
        assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);
    }

    @Test
    void findByOwner_IdAndEndIsAfterAndStartIsBeforeOrderByIdAsc() {
        List<Booking> bookings = bookingRepository.findByOwner_IdAndEndIsAfterAndStartIsBeforeOrderByIdAsc(1L, now());
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getOwner().getId(), 1L);
    }


    @Test
    void findByItem_Id() {
        List<Booking> bookings = bookingRepository.findByItem_Id(1L);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getId(), 1L);
    }

    @Test
    void findByOwnerAndStatus() {
        List<Booking> bookings = bookingRepository.findByOwnerAndStatus(1L, Status.WAITING);
        assertEquals(bookings.size(), 3);
        assertEquals(bookings.get(0).getStatus(), Status.WAITING);
    }

    @Test
    void findByBooker_IdAndStatus() {
        List<Booking> bookings = bookingRepository.findByBooker_IdAndStatus(2L, Status.WAITING);
        assertEquals(bookings.size(), 3);
        assertEquals(bookings.get(0).getStatus(), Status.WAITING);
    }

    @Test
    void findByItem_IdAndBooker_idAndStatus() {
        Optional<List<Booking>> bookings = bookingRepository.findByItem_IdAndBooker_idAndStatus(1L, 2L, Status.WAITING);
        assertEquals(bookings.get().size(), 1);
        assertEquals(bookings.get().get(0).getItem().getId(), 1L);
        assertEquals(bookings.get().get(0).getBooker().getId(), 2L);
        assertEquals(bookings.get().get(0).getStatus(), Status.WAITING);
    }


    @AfterEach
    private void deleteBookings() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();

    }
}