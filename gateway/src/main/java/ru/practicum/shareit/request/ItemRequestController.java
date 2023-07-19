package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader Map<String, String> headers,
                                      @RequestHeader("X-Sharer-User-Id") long userId,
                                      @RequestBody ItemRequestDto itemRequestDto) {
        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        itemRequestDto.setCreated(LocalDateTime.now());
        return itemRequestClient.add(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@RequestHeader Map<String, String> headers,
                                      @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("requestId") Long requestId) {
        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return itemRequestClient.getRequest(userId, requestId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwn(@RequestHeader Map<String, String> headers,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return itemRequestClient.getOwn(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader Map<String, String> headers,
                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Valid @RequestParam(name = "from") Optional<Long> from,
                                         @Valid @RequestParam(name = "size") Optional<Long> size) {


        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (from.isEmpty() && size.isEmpty()) {
            return itemRequestClient.getAll(userId);
        }

        if (from.get() < 0 || size.get() < 0) {
            log.info("Значение from или size не может быть меньше нуля");
            throw new ValidationException("from или size не может быть меньше нуля: Введите положительные значения.");
        }
        if (from.get() == 0 && size.get() == 0) {
            log.info("Значение from и size не могут быть равны нулю");
            throw new ValidationException("from и size не могут быть равны нулю : Введите положительные значения.");
        }

        return itemRequestClient.getAllPaged(userId, from.get(), size.get());


    }
}

