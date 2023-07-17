package ru.practicum.shareit.user.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    ResponseEntity<UserDto> updateUser(UserDto user, Long id);

    List<UserDto> getAllUsers();

    UserDto saveUser(UserDto userDto);

    UserDto getUser(Long id);

    void delete(Long id);
}
