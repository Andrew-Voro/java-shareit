package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> add(@RequestHeader Map<String, String> headers,
                                              @RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestBody ItemRequestDto itemRequestDto) {
        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        itemRequestDto.setCreated(LocalDateTime.now());
        return new ResponseEntity<>(itemRequestService.add(userId, itemRequestDto), HttpStatus.OK);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> get(@RequestHeader Map<String, String> headers,
                                              @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("requestId") Long requestId) {
        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(itemRequestService.getRequest(userId, requestId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getOwn(@RequestHeader Map<String, String> headers,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(itemRequestService.getOwn(userId), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAll(@RequestHeader Map<String, String> headers,
                                                       @RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @Valid @RequestParam(name = "from") Optional<Long> from,
                                                       @Valid @RequestParam(name = "size") Optional<Long> size) {


        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (from.isEmpty() && size.isEmpty()) {
            return new ResponseEntity<>(itemRequestService.getAll(userId), HttpStatus.OK);
        }

        if (from.get() < 0 || size.get() < 0) {
            log.info("Значение from или size не может быть меньше нуля");
            throw new ValidationException("from или size не может быть меньше нуля: Введите положительные значения.");
        }
        if (from.get() == 0 && size.get() == 0) {
            log.info("Значение from и size не могут быть равны нулю");
            throw new ValidationException("from и size не могут быть равны нулю : Введите положительные значения.");
        }

        return new ResponseEntity<>(itemRequestService.getAllPaged(userId, from.get(), size.get()), HttpStatus.OK);


    }
}

