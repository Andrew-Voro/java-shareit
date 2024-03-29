package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoBack;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.handler.exception.ValidationException;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.time.LocalDateTime.now;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> add(@RequestHeader Map<String, String> headers,
                                       @RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestBody BookingDto bookingDto) {

        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            throw new ValidationException("Время начала/окончания бронирования не может быть null");
        }

        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Метод add нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (bookingDto.getStart().isBefore(now())) {
            throw new ValidationException("Время начала бронирования не может быть в прошлом");
        }
        if (bookingDto.getEnd().isBefore(now())) {
            throw new ValidationException("Время окончания бронирования не может быть в прошлом");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("Время окончания бронирования не может быть раньше времени его начала");
        }
        if (bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new ValidationException("Время окончания бронирования не может быть равно времени его начала");
        }


        log.info("Booking добавление.");
        bookingDto.setBooker(userId);
        return new ResponseEntity<>(bookingService.addNewBooking(bookingDto), HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDtoBack> getBooking(@RequestHeader Map<String, String> headers,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("bookingId") Long bookingId,
                                                     @Valid @RequestParam(name = "approved") Optional<Boolean> approved) {

        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (bookingId < 0) {
            log.info("Значение bookingId не может быть меньше нуля");
            throw new ValidationException("getItem: Введите положительный bookingId.");
        }

        log.info("Предмет с itemId:" + bookingId + " запрошен.");
        return new ResponseEntity<>(bookingService.getBooking(bookingId, userId), HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDtoBack>> getBookingOwner(@RequestHeader Map<String, String> headers,
                                                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                                                @Valid @RequestParam(name = "state") Optional<String> state,
                                                                @Valid @RequestParam(name = "from") Optional<Long> from,
                                                                @Valid @RequestParam(name = "size") Optional<Long> size
    ) {
        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (userId < 0) {
            log.info("Значение itemId не может быть меньше нуля");
            throw new ValidationException("getItem: Введите положительный itemId.");
        }

        if (from.isPresent() && size.isPresent()) {
            if (from.get() < 0 || size.get() < 0) {
                log.info("own:Значение from или size не может быть меньше нуля");
                throw new ValidationException("from или size не может быть меньше нуля: Введите положительные значения.");
            }
            if (from.get() == 0 && size.get() == 0) {
                log.info("Значение from и size не могут быть равны нулю");
                throw new ValidationException("from и size не могут быть равны нулю : Введите положительные значения.");
            }

            if (from.get() > 0 && size.get() > 0) {
                return new ResponseEntity<>(bookingService.getAllOwnBookingPaged(userId, from.get(), size.get()), HttpStatus.OK);
            }
        }
        LocalDateTime now = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        if (state.isEmpty() || state.get().equals(State.ALL.toString())) {
            return new ResponseEntity<>(bookingService.getBookingOwner(userId), HttpStatus.OK);
        } else if (state.get().equals(State.PAST.toString())) {
            return new ResponseEntity<>(bookingService.getBookingOwnerPast(userId, now), HttpStatus.OK);

        } else if (state.get().equals(State.CURRENT.toString())) {
            return new ResponseEntity<>(bookingService.getBookingOwnerCurrent(userId, now), HttpStatus.OK);
        } else if (state.get().equals(State.FUTURE.toString())) {
            return new ResponseEntity<>(bookingService.getBookingOwnerFuture(userId, now), HttpStatus.OK);

        } else if (state.get().equals(Status.WAITING.toString()) || state.get().equals(Status.REJECTED.toString())) {
            return new ResponseEntity<>(bookingService.getBookingOwnerStatus(userId, Status.valueOf(state.get())), HttpStatus.OK);

        } else {

            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }

    }

    @GetMapping
    public ResponseEntity<List<BookingDtoBack>> getBookingBooker(@RequestHeader Map<String, String> headers,
                                                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                                                 @Valid @RequestParam(name = "state") Optional<String> state,
                                                                 @Valid @RequestParam(name = "from") Optional<Long> from,
                                                                 @Valid @RequestParam(name = "size") Optional<Long> size) {
        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (userId < 0) {
            log.info("Значение itemId не может быть меньше нуля");
            throw new ValidationException("getItem: Введите положительный itemId.");
        }

        if (from.isPresent() && size.isPresent()) {
            if (from.get() < 0 || size.get() < 0) {
                log.info("booker:Значение from или size не может быть меньше нуля");
                throw new ValidationException("from или size не может быть меньше нуля: Введите положительные значения.");
            }
            if (from.get() == 0 && size.get() == 0) {
                log.info("Значение from и size не могут быть равны нулю");
                throw new ValidationException("from и size не могут быть равны нулю : Введите положительные значения.");
            }
            if (from.get() > 0 && size.get() > 0) {
                return new ResponseEntity<>(bookingService.getAllBookingPaged(userId, from.get(), size.get()), HttpStatus.OK);
            }
        }
        LocalDateTime now = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

        if (state.isEmpty() || state.get().equals(State.ALL.toString())) {
            return new ResponseEntity<>(bookingService.getBookingBooker(userId), HttpStatus.OK);
        } else if (state.get().equals(State.PAST.toString())) {
            return new ResponseEntity<>(bookingService.getBookingBookerPast(userId, now), HttpStatus.OK);

        } else if (state.get().equals(State.CURRENT.toString())) {
            return new ResponseEntity<>(bookingService.getBookingBookerCurrent(userId, now), HttpStatus.OK);

        } else if (state.get().equals(State.FUTURE.toString())) {
            return new ResponseEntity<>(bookingService.getBookingBookerFuture(userId, now), HttpStatus.OK);

        } else if (state.get().equals(Status.WAITING.toString()) || state.get().equals(Status.REJECTED.toString())) {
            return new ResponseEntity<>(bookingService.getBookingBookerStatus(userId, Status.valueOf(state.get())), HttpStatus.OK);

        } else {
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }

    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDtoBack> updateBooking(@PathVariable("bookingId") Long bookingId,
                                                        @RequestHeader("X-Sharer-User-Id") long userId,
                                                        @Valid @RequestParam(name = "approved") Optional<Boolean> approved) {
        Status status;
        if (approved.isEmpty()) {
            throw new ValidationException("Праметр approved не может быть null");
        } else if (approved.get().booleanValue() == true) {
            status = Status.APPROVED;
        } else {
            status = Status.REJECTED;
        }
        return new ResponseEntity<>(bookingService.bookingUpdate(bookingId, userId, status), HttpStatus.OK);

    }
}
