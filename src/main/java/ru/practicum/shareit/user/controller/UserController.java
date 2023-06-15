package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userStorage;
    private final UserService userService;


    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody UserDto user) {
        if (userStorage.findAll().stream().map(User::getEmail).collect(Collectors.toList()).contains(user.getEmail())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        if (user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(UserMapper.toUserDto(userStorage.create(UserMapper.toDtoUser(user))), HttpStatus.OK);
    }


    @GetMapping
    public  ResponseEntity<Collection<UserDto>> findAll() {
        return new ResponseEntity<>(userStorage.findAll().stream().map(UserMapper::toUserDto)
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long id) {
        if (id < 0) {
            throw new ValidationException("getUser: Введите положительный id.");
        }
        if(!userStorage.findAll().stream().map(User::getId).collect(Collectors.toList()).contains(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(UserMapper.toUserDto(userStorage.findUserById(id)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        if (id < 0) {
            throw new ValidationException("delete: Введите положительный id.");
        }
        userStorage.delete(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id, @RequestBody Map<String, Object> fields) {

        if (fields.containsKey("email")&&userStorage.findAll().stream().map(User::getEmail)
                .collect(Collectors.toList()).contains(fields.get("email"))) {
            if(userStorage.findUserById(id).getEmail().equals(fields.get("email"))) {
                return  getUser(id);
            }
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return userService.updateUser(fields, id);
    }


}
