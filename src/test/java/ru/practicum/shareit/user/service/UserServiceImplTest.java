package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<User> userDtoArgumentCaptor;


    @Test
    void getUserFoundReturn() {
        long userId = 0L;
        User expectedUser = new User();
        expectedUser.setEmail("e@mail.ru");

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        UserDto actualUser = userService.getUser(userId);
        assertEquals(UserMapper.toUserDto(expectedUser), actualUser);

    }

    @Test
    void getUserNotFoundReturn() {
        long userId = 0L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userService.getUser(userId));
    }


    @Test
    void saveUser() {
        User userToSave = new User();
        userToSave.setEmail("e@mail.ru");

        when(userRepository.save(userToSave)).thenReturn(userToSave);

        UserDto actualUser = userService.saveUser(UserMapper.toUserDto(userToSave));
        assertEquals(UserMapper.toUserDto(userToSave), actualUser);
        verify(userRepository).save(userToSave);
    }


    @Test
    void updateUser() {
        Long userId = 1L;

        User oldUser = new User();
        oldUser.setName("al");
        oldUser.setEmail("o@em.com");
        oldUser.setId(userId);
        oldUser.setId(userId);

        User newUser = new User();
        newUser.setName("all");
        newUser.setEmail("ol@em.com");
        newUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(oldUser)).thenReturn(oldUser);
        UserDto actualUser = userService.updateUser(UserMapper.toUserDto(newUser), userId).getBody();
        verify(userRepository).save(userDtoArgumentCaptor.capture());
        User saveUser = userDtoArgumentCaptor.getValue();
        assertEquals("all", saveUser.getName());
        assertEquals("ol@em.com", saveUser.getEmail());
    }

    @Test
    void getAllUsers() {
        User expectedUser = new User();
        expectedUser.setEmail("e@mail.ru");

        List<User> users = new ArrayList<>();
        users.add(expectedUser);

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> actualUsers = userService.getAllUsers();
        assertEquals(UserMapper.toUserDto(expectedUser), actualUsers.get(0));
    }

    @Test
    void delete() {
        Long userId = 0L;
        userService.delete(userId);
        verify(userRepository).deleteById(userId);
    }

}