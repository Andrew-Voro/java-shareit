package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ItemDto> add(@RequestHeader Map<String, String> headers,
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

        userService.getUser(userId);
        log.info("Предмет добавление.");
        return new ResponseEntity<>(itemService.addNewItem(userId, item), HttpStatus.OK);
    }

    @GetMapping
    public List<ItemDto> get(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getItems(userId);
    }


    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@RequestHeader Map<String, String> headers,
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
        return new ResponseEntity<>(itemService.getItem(itemId, userId), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable("itemId") Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody Map<String, Object> fields,
                                              @RequestHeader Map<String, String> headers
    ) {
        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Метод updateItem нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        userService.getUser(userId).getId();
        if (!itemService.getItem(itemId, userId).getOwner().equals(userId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        log.info("Предмет с id: " + itemId + " обновление.");
        return new ResponseEntity<>(itemService.updateItem(fields, userId, itemId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchByNameOrDescription(@Valid @RequestParam(name = "text") String text) {
        log.info("Поиск предмета по названию:" + text);
        return new ResponseEntity<>(itemService.searchByNameOrDescription(text), HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable("itemId") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(itemId, userId, commentDto);
    }
}
