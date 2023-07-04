package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Вернули список пользователей.");
        return userService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<UserDto> saveNewUser(@RequestBody UserDto user) {

        if (user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
            log.info("Некорректный почтовый адрес");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        log.info("Пользователь с почтой: " + user.getEmail() + " создан.");

        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id) {
        if (id < 0) {
            log.info("Введите положительный id.");
            throw new ValidationException("getUser: Введите положительный id.");
        }
        log.info("Пользователь с  id: " + id + " запрошен.");
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id, @RequestBody Map<String, Object> fields) {

        if (fields.containsKey("email") && userService.getAllUsers().stream().map(UserDto::getEmail)
                .collect(Collectors.toList()).contains(fields.get("email"))) {
            if (userService.getUser(id).getEmail().equals(fields.get("email"))) {
                log.info("Изменения уже были внесены.");
                return getUser(id);
            }
            log.info("Несоответствие id и почты пользователя.");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        log.info("Данные пользователя с  id: " + id + " обновлены.");
        return userService.updateUser(fields, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        if (id < 0) {
            log.info("Введите положительный id.");
            throw new ValidationException("delete: Введите положительный id.");
        }
        userService.getUser(id);
        log.info("Пользователь с  id: " + id + " удален.");
        userService.delete(id);
    }
}
