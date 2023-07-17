package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoBack;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public Booking addNewBooking(BookingDto bookingDto) {
        bookingDto.setStatus(Status.WAITING);
        User user = userRepository.findById(bookingDto.getBooker()).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Item item = itemRepository.findById(bookingDto.getItem()).orElseThrow(() -> new ObjectNotFoundException("Item not found"));
        if (user.getId().equals(item.getOwner().getId())) {
            throw new ObjectNotFoundException("Хозяин вещи не может ее бронировать");
        }
        if (item.getAvailable().equals(Boolean.FALSE)) {
            throw new ValidationException("Вещь не доступна");
        }

        Booking booking = bookingRepository.save(BookingMapper.toDtoBooking(bookingDto, user, item));
        return booking;
    }

    @Transactional
    @Override
    public BookingDtoBack getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("Booking not found"));
        if (!(booking.getBooker().getId().equals(userId) || booking.getItem().getOwner().getId().equals(userId))) {
            throw new ObjectNotFoundException("Пользователь не является инициатором бронирования или хозяином вещи");
        }
        return BookingMapper.toBookingDtoBack(booking);

    }

    @Transactional
    @Override
    public List<BookingDtoBack> getBookingOwner(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        return bookingRepository.findByOwner(userId).stream().map(BookingMapper::toBookingDtoBack).collect(Collectors.toList());
    }


    @Transactional
    @Override
    public List<BookingDtoBack> getBookingBooker(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        return bookingRepository.findByBooker(userId).stream().map(BookingMapper::toBookingDtoBack).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookingDtoBack> getAllOwnBookingPaged(Long userId, Long from, Long size) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());

        return bookingRepository.findByOwnerPaged(userId, page).stream().map(BookingMapper::toBookingDtoBack).collect(Collectors.toList());
    }


    @Transactional
    @Override
    public List<BookingDtoBack> getAllBookingPaged(Long userId, Long from, Long size) {
        PageRequest page = PageRequest.of(from.intValue() > 0 ? from.intValue() / size.intValue() : 0, size.intValue());
        return bookingRepository.findByBookerPaged(userId, page).stream().map(BookingMapper::toBookingDtoBack).collect(Collectors.toList());
    }


    @Transactional
    @Override
    public List<BookingDtoBack> getBookingBookerFuture(Long bookerId, LocalDateTime start) {
        return bookingRepository.findByBooker_IdAndStartIsAfterOrderByIdDesc(bookerId, start).stream()
                .map(BookingMapper::toBookingDtoBack).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookingDtoBack> getBookingBookerPast(Long bookerId, LocalDateTime now) {
        return bookingRepository.findByBooker_IdAndEndIsBeforeOrderByIdDesc(bookerId, now).stream()
                .map(BookingMapper::toBookingDtoBack).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookingDtoBack> getBookingBookerCurrent(Long bookerId, LocalDateTime now) {
        return bookingRepository.findByBooker_IdAndEndIsAfterAndStartIsBeforeOrderByIdAsc(bookerId, now, now).stream()
                .map(BookingMapper::toBookingDtoBack).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookingDtoBack> getBookingOwnerFuture(Long owner, LocalDateTime now) {
        return bookingRepository.findByOwnerAndStartIsAfter(owner, now).stream()
                .map(BookingMapper::toBookingDtoBack).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookingDtoBack> getBookingOwnerPast(Long owner, LocalDateTime now) {
        return bookingRepository.findByOwnerAndEndIsBefore(owner, now).stream()
                .map(BookingMapper::toBookingDtoBack).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookingDtoBack> getBookingOwnerCurrent(Long owner, LocalDateTime now) {
        return bookingRepository.findByOwner_IdAndEndIsAfterAndStartIsBeforeOrderByIdAsc(owner, now).stream()
                .map(BookingMapper::toBookingDtoBack).collect(Collectors.toList());
    }


    @Transactional
    @Override
    public BookingDtoBack bookingUpdate(Long bookingId, Long userId, Status status) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("Booking not found"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Пользователь не является хозяином вещи, отклонено изменение статуса.");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("Статус approved,повторные разрешения не требуются");
        }
        booking.setStatus(status);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDtoBack(booking);
    }

    @Transactional
    @Override
    public List<BookingDtoBack> getBookingOwnerStatus(Long userId, Status status) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        return bookingRepository.findByOwnerAndStatus(userId, status).stream().map(BookingMapper::toBookingDtoBack).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public List<BookingDtoBack> getBookingBookerStatus(Long userId, Status status) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        return bookingRepository.findByBooker_IdAndStatus(userId, status).stream().map(BookingMapper::toBookingDtoBack).collect(Collectors.toList());
    }
}
