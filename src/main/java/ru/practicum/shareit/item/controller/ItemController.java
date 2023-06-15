package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserRepository userStorage;
    private final ItemRepository itemStorage;

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
            throw new ValidationException("getItem: Введите положительный itemId.");
        }


        return new ResponseEntity<>(ItemMapper.toItemDto(itemStorage.getItem(itemId)), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<ItemDto> add(@RequestHeader Map<String, String> headers,
                                       @RequestHeader("X-Sharer-User-Id") long userId,
                                       @RequestBody ItemDto item) {
        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!userStorage.findAll().stream().map(User::getId).collect(Collectors.toList()).contains(userId)) {
            log.info("Нет пользователя с id " + userId + " .");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (item.getName().isBlank()) {
            log.info("Поле name пусто.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(itemService.addNewItem(userId, item), HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable("itemId") Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody Map<String, Object> fields,
                                              @RequestHeader Map<String, String> headers
    ) {

        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!userStorage.findAll().stream().map(User::getId).collect(Collectors.toList()).contains(userId)) {
            log.info("Нет пользователя с id " + userId + " .");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!itemService.getItems(userId).stream().map(ItemDto::getId).collect(Collectors.toList()).contains(itemId)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity<>(itemService.updateItem(fields, userId, itemId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchByNameOrDescription(@RequestParam(name = "text") String text) {
        return new ResponseEntity<>(itemService.searchByNameOrDescription(text), HttpStatus.OK);
    }
}
