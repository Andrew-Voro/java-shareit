package ru.practicum.shareit.user.service;

import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Map;

public interface UserService {
    ResponseEntity<UserDto> updateUser(Map<String, Object> fields, Long id);
}
