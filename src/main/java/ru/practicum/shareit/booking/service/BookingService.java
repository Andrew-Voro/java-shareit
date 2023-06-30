package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoBack;
import ru.practicum.shareit.booking.model.Booking;


import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    Booking addNewBooking(BookingDto bookingDto);

    BookingDtoBack getBooking(Long bookingId, Long userId);

    List<BookingDtoBack> getBookingOwner(Long userId);

    List<BookingDtoBack> getBookingBooker(Long userId);

    List<BookingDtoBack> getBookingBookerFuture(Long userId, LocalDateTime now);

    List<BookingDtoBack> getBookingOwnerFuture(Long owner, LocalDateTime now);

    List<BookingDtoBack> getBookingOwnerPast(Long userId, LocalDateTime now);

    List<BookingDtoBack> getBookingOwnerCurrent(Long userId, LocalDateTime now);

    BookingDtoBack bookingUpdate(Long bookingId, Long userId, Status status);

    List<BookingDtoBack> getBookingOwnerStatus(Long userId, Status status);

    List<BookingDtoBack> getBookingBookerStatus(Long userId, Status status);

    List<BookingDtoBack> getBookingBookerPast(Long userId, LocalDateTime now);

    List<BookingDtoBack> getBookingBookerCurrent(Long userId, LocalDateTime now);

    List<BookingDtoBack> getAllOwnBookingPaged(Long userId,Long from,Long size);

    List<BookingDtoBack> getAllBookingPaged(Long userId,Long from,Long size);
}