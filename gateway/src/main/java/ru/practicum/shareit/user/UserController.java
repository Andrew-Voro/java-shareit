package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;


@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Вернули список пользователей.");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> saveNewUser(@RequestBody UserDto user) {

        if (user.getEmail() == null) {
            throw new ValidationException("Not found email in body of request ");
        }

        if (user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
            log.info("Некорректный почтовый адрес");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        log.info("Пользователь с почтой: " + user.getEmail() + " создан.");

        return userClient.saveUser(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") Long id) {
        if (id < 0) {
            log.info("Введите положительный id.");
            throw new ValidationException("getUser: Введите положительный id.");
        }
        log.info("Пользователь с  id: " + id + " запрошен.");
        return userClient.getUser(id);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long id, @RequestBody UserDto user) {

        log.info("Данные пользователя с  id: " + id + " обновлены.");
        return userClient.updateUser(user, id);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        if (id < 0) {
            log.info("Введите положительный id.");
            throw new ValidationException("delete: Введите положительный id.");
        }
        log.info("Пользователь с  id: " + id + " удален.");
        userClient.delete(id);
    }
}
