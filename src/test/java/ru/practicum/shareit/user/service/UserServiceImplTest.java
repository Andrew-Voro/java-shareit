package ru.practicum.shareit.user.service;

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

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<UserDto> userDtoArgumentCaptor;



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

    }
}