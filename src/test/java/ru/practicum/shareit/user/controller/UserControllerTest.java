package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        UserDto user = UserDto.builder().email("user444@user.com").name("Марк").build();
        UserDto user2 = userController.saveNewUser(user).getBody();
        assertNotNull(user2.getId());
        assertEquals(user2.getName(), user.getName());
    }

    @Test
    public void updateUserTest() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("name", "Марк Юрьевич");
        long id = 1L;
        UserDto user = UserDto.builder().email("user444@user.com").name("Марк").build();
        userController.saveNewUser(user).getBody();
        UserDto user3 = userController.updateUser(id, fields).getBody();
        assertEquals(user3.getName(), "Марк Юрьевич");
        userController.delete(1L);
    }

}