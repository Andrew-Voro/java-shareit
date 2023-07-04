package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        log.info("Вернули список пользователей.");
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> saveNewUser(@RequestBody UserDto user) {

        if (user.getEmail() == null) {
            throw new ValidationException("Not found email in body of request ");
        }

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
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id,@Valid @RequestBody UserDto user) {

        if ((!(user.getEmail() == null)) && userService.getAllUsers().stream().map(UserDto::getEmail)
                .collect(Collectors.toList()).contains(user.getEmail())) {
            if (userService.getUser(id).getEmail().equals(user.getEmail())) {
                log.info("Изменения уже были внесены.");
                return getUser(id);
            }
            log.info("Несоответствие id и почты пользователя.");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        log.info("Данные пользователя с  id: " + id + " обновлены.");
        return userService.updateUser(user, id);
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
