package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    public void contextLoads() throws Exception {
        assertThat(userController).isNotNull();
    }

    @Test
    public void createUserTest() {
        UserDto user = UserDto.builder().email("user@user.com").name("Марк").build();
        UserDto user2 = userController.create(user).getBody();
        assertNotNull(user2.getId());
        assertEquals(user2.getName(), user.getName());
        userController.delete(user2.getId());
    }

    @Test
    public void updateUserTest() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("name", "Марк Юрьевич");
        long id = 1l;
        if (userController.getUser(id).getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            UserDto user = UserDto.builder().email("user@user.com").name("Марк").build();
            userController.create(user).getBody();
        }

        UserDto user3 = userController.updateUser(id, fields).getBody();
        assertEquals(user3.getName(), "Марк Юрьевич");
        userController.delete(1l);
    }

    @Test
    public void deleteTest() {
        if ((int) (Math.random() * 2) == 0) {
            UserDto user = UserDto.builder().email("user@user.com").name("Марк").build();
            userController.create(user).getBody();
        }
        long id = 1l;
        if (userController.findAll().getBody().stream().map(UserDto::getId).collect(Collectors.toList()).contains(id)) {
            userController.delete(id);
            assertFalse(userController.findAll().getBody().stream().map(UserDto::getId)
                    .collect(Collectors.toList()).contains(id));
        } else {
            final ValidationException exception = assertThrows(
                    ValidationException.class,
                    new Executable() {
                        @Override
                        public void execute() {
                            userController.delete(id);
                        }
                    });
            assertEquals("Delete: ValidationException пользователь c  id = " + id +
                            " отсутствует в базе. ",
                    exception.getMessage());
        }

    }

}