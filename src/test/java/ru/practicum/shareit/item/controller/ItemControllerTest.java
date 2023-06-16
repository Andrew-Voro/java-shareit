package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemControllerTest {
    @Autowired
    private ItemController controller;
    @Autowired
    private UserController userController;

    @Test
    public void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
        assertThat(userController).isNotNull();
    }

    @Test
    public void createItemTest() {
        UserDto user = UserDto.builder().email("user@user.com").name("Марк").build();
        ItemDto item = ItemDto.builder().available(true).description("Отвертка аккумуляторная")
                .name("Ответка").owner(1l).build();
        Map<String, String> headers = new HashMap<>();
        headers.put("x-sharer-user-id", "1");
        UserDto user2 = userController.create(user).getBody();
        ItemDto item2 = controller.add(headers, user2.getId(), item).getBody();
        assertNotNull(item2.getId());
        assertNotNull(item2.getOwner());
        assertEquals(item2.getName(), item.getName());
    }

    @Test
    public void updateItemTest() {
        Map<String, Object> fields = new HashMap<>();
        fields.put("name", "Марк Юрьевич");
        Map<String, Object> fieldsItem = new HashMap<>();
        fieldsItem.put("name", "Отвертка плоская");
        long id = 1l;
        long itemId = 1l;
        Map<String, String> headers = new HashMap<>();
        headers.put("x-sharer-user-id", "1");
        Map<String, String> headersItem = new HashMap<>();
        headersItem.put("x-sharer-user-id", "1");

        if (userController.getUser(id).getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            UserDto user = UserDto.builder().email("user@user.com").name("Марк").build();
            userController.create(user).getBody();
        }
        ItemDto item = ItemDto.builder().available(true).description("Отвертка аккумуляторная")
                .name("Ответка").owner(itemId).build();
        controller.add(headers, id, item).getBody();
        ItemDto item2 = controller.updateItem(itemId, id, fieldsItem, headersItem).getBody();
        assertEquals(item2.getName(), "Отвертка плоская");
        userController.delete(1l);
    }


}