package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)

@WebMvcTest({BookingController.class})
class BookingControllerIT {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private ObjectMapper objectMapper;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;


    @SneakyThrows
    @Test
    void add() {

        Long userId = 0L;
        Long itemId = 0L;
        Long bookingId = 0L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDtoCreate = BookingDto.builder().item(itemId).booker(userId)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        Item itemAdd = Item.builder().available(true).description("thing").name("mock").id(itemId).build();
        User userAdd = User.builder().email("a@n.com").id(userId).name("nick").build();
        Booking bookingBack = Booking.builder().item(itemAdd).booker(userAdd)
                .status(Status.WAITING).start(start).end(end).id(bookingId).build();
        Mockito.when(bookingService.addNewBooking(any(BookingDto.class))).thenReturn(bookingBack);

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                .param("x-sharer-user-id", "0")
                .content(objectMapper.writeValueAsString(bookingDtoCreate))
                .header("x-sharer-user-id", userId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoCreate.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDtoCreate.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")).toString())))
                .andExpect(jsonPath("$.end", is(bookingDtoCreate.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")).toString())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingBack), result);
    }

    @SneakyThrows
    @Test
    void getBooking() {
        Long bookingId = 0L;
        Long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", bookingId)
                .param("x-sharer-user-id", "0")
                .param("approved", "true")
                .header("x-sharer-user-id", userId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(bookingService).getBooking(bookingId, userId);
    }

    @SneakyThrows
    @Test
    void getBookingOwner() {
        Long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                .param("x-sharer-user-id", "0")
                .header("x-sharer-user-id", userId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(bookingService).getBookingOwner(userId);
    }

    @SneakyThrows
    @Test
    void getBookingBooker() {
        Long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                .param("x-sharer-user-id", "0")
                .header("x-sharer-user-id", userId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(bookingService).getBookingBooker(userId);
    }

    @SneakyThrows
    @Test
    void updateBooking() {
        Long userId = 0L;
        Long bookingId = 0L;
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                .contentType("application/json")
                .param("approved", "true")
                .header("x-sharer-user-id", userId))
                .andExpect(status().isOk());
        verify(bookingService).bookingUpdate(bookingId, userId, Status.APPROVED);
    }
}

