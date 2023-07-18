package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader Map<String, String> headers,
                                      @RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody ItemDto item) {
        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Метод add нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (item.getName().isBlank()) {
            log.info("Поле name пусто.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (item.getAvailable() == null) {
            log.info("Поле available null.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (item.getDescription() == null) {
            log.info("Поле description null.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        log.info("Предмет добавление.");
        return itemClient.addNewItem(userId, item);
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemClient.getItems(userId);
    }


    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader Map<String, String> headers,
                                          @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long itemId) {
        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (itemId < 0) {
            log.info("Значение itemId не может быть меньше нуля");
            throw new ValidationException("getItem: Введите положительный itemId.");
        }
        log.info("Предмет с itemId:" + itemId + " запрошен.");
        return itemClient.getItem(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable("itemId") Long itemId,
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody Map<String, Object> fields,
                                             @RequestHeader Map<String, String> headers
    ) {
        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Метод updateItem нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        log.info("Предмет с id: " + itemId + " обновление.");
        return itemClient.updateItem(itemId, userId, fields);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByNameOrDescription(@Valid @RequestParam(name = "text") String text) {
        log.info("Поиск предмета по названию:" + text);
        return itemClient.searchByNameOrDescription(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable("itemId") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(itemId, userId, commentDto);
    }
}
