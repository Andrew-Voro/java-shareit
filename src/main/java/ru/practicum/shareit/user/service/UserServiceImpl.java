package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;


    @Transactional
    public ResponseEntity<UserDto> updateUser(UserDto userDto, Long id) {
        User user = repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        return new ResponseEntity<>(UserMapper.toUserDto(repository.save(user)), HttpStatus.OK);
    }


    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = repository.findAll();
        return UserMapper.mapToUserDto(users);
    }

    @Transactional
    @Override
    public UserDto saveUser(UserDto userDto) {
        User user = repository.save(UserMapper.toDtoUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getUser(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User not found"));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}

