package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest({ItemController.class})
class ItemControllerTest {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private ObjectMapper objectMapper;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;


    @SneakyThrows
    @Test
    void addWithoutHeaderException() {
        Long userId = 0L;
        UserDto userDtoCreate = new UserDto();
        userDtoCreate.setEmail("a@n.com");
        userDtoCreate.setId(userId);
        ItemDto itemDtoAdd = ItemDto.builder().available(true).description("thing").name("mock").build();
        Mockito.when(userService.getUser(anyLong())).thenReturn(userDtoCreate);
        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .content(objectMapper.writeValueAsString(itemDtoAdd))
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addNewItem(userId, itemDtoAdd);
    }

    @SneakyThrows
    @Test
    void addNameIsBlank() {
        Long userId = 0L;
        UserDto userDtoCreate = new UserDto();
        userDtoCreate.setEmail("a@n.com");
        userDtoCreate.setId(userId);
        ItemDto itemDtoAdd = ItemDto.builder().available(true).description("thing").name("").build();
        Mockito.when(userService.getUser(anyLong())).thenReturn(userDtoCreate);
        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .header("x-sharer-user-id", userDtoCreate.getId())
                .content(objectMapper.writeValueAsString(itemDtoAdd))
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addNewItem(userId, itemDtoAdd);
    }

    @SneakyThrows
    @Test
    void addAvailableIsNull() {
        Long userId = 0L;
        UserDto userDtoCreate = new UserDto();
        userDtoCreate.setEmail("a@n.com");
        userDtoCreate.setId(userId);
        ItemDto itemDtoAdd = ItemDto.builder().description("thing").name("thing").build();
        Mockito.when(userService.getUser(anyLong())).thenReturn(userDtoCreate);
        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .header("x-sharer-user-id", userDtoCreate.getId())
                .content(objectMapper.writeValueAsString(itemDtoAdd))
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).addNewItem(userId, itemDtoAdd);
    }

    @SneakyThrows
    @Test
    void addDescriptionIsNull() {
        Long userId = 0L;
        UserDto userDtoCreate = new UserDto();
        userDtoCreate.setEmail("a@n.com");
        userDtoCreate.setId(userId);
        ItemDto itemDtoAdd = ItemDto.builder().available(true).name("thing").build();
        Mockito.when(userService.getUser(anyLong())).thenReturn(userDtoCreate);
        mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .header("x-sharer-user-id", userDtoCreate.getId())
                .content(objectMapper.writeValueAsString(itemDtoAdd))
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addNewItem(userId, itemDtoAdd);
    }


    @SneakyThrows
    @Test
    void add() {
        Long userId = 0L;
        UserDto userDtoCreate = new UserDto();
        userDtoCreate.setEmail("a@n.com");
        userDtoCreate.setId(userId);
        ItemDto itemDtoAdd = ItemDto.builder().available(true).description("thing").name("mock").build();
        Mockito.when(itemService.addNewItem(anyLong(), any(ItemDto.class))).thenReturn(itemDtoAdd);
        Mockito.when(userService.getUser(anyLong())).thenReturn(userDtoCreate);
        String result = mockMvc.perform(MockMvcRequestBuilders.post("/items")
                .param("x-sharer-user-id", "0")
                .content(objectMapper.writeValueAsString(itemDtoAdd))
                .header("x-sharer-user-id", userDtoCreate.getId())
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoAdd.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoAdd.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoAdd.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoAdd.getAvailable())))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemDtoAdd), result);
    }

    @SneakyThrows
    @Test
    void get() {

        Long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                .header("x-sharer-user-id", userId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemService).getItems(userId);
    }

    @SneakyThrows
    @Test
    void getItemWithoutHeaderException() {
        Long itemId = 0L;
        Long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getItem(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getItemItemIdLessZero() {
        Long itemId = -1L;
        Long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                .param("x-sharer-user-id", "0")
                .header("x-sharer-user-id", userId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verify(itemService, never()).getItem(itemId, userId);
    }

    @SneakyThrows
    @Test
    void getItem() {
        Long itemId = 0L;
        Long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                .param("x-sharer-user-id", "0")
                .header("x-sharer-user-id", userId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemService).getItem(itemId, userId);
    }


    @SneakyThrows
    @Test
    void updateItem() {
        Long userId = 0L;
        UserDto userDto = new UserDto();
        userDto.setEmail("a@n.com");
        userDto.setId(userId);
        Long itemId = 0L;
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", "1");
        fields.put("name", "thing");
        fields.put("description", "new thing");
        fields.put("available", false);
        ItemDto itemDtoUpdate = ItemDto.builder().available(true).owner(userId).id(1L)
                .description("new thing").name("thing").build();

        Mockito.when(userService.getUser(userId)).thenReturn(userDto);
        Mockito.when(itemService.getItem(itemId, userId)).thenReturn(itemDtoUpdate);
        mockMvc.perform(patch("/items/{itemId}", itemId)
                .contentType("application/json")
                .header("x-sharer-user-id", userId)
                .content(objectMapper.writeValueAsString(fields)))
                .andExpect(status().isOk());
        verify(itemService).updateItem(fields, userId, itemId);
    }

    @SneakyThrows
    @Test
    void updateItemWithoutHeaderException() {
        Long userId = 0L;
        UserDto userDto = new UserDto();
        userDto.setEmail("a@n.com");
        userDto.setId(userId);
        Long itemId = 0L;
        Map<String, Object> fields = new HashMap<>();
        fields.put("id", "1");
        fields.put("name", "thing");
        fields.put("description", "new thing");
        fields.put("available", false);
        ItemDto itemDtoUpdate = ItemDto.builder().available(true).owner(userId).id(1L)
                .description("new thing").name("thing").build();

        Mockito.when(userService.getUser(userId)).thenReturn(userDto);
        Mockito.when(itemService.getItem(itemId, userId)).thenReturn(itemDtoUpdate);
        mockMvc.perform(patch("/items/{itemId}", itemId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(fields)))
                .andExpect(status().isBadRequest());
        verify(itemService, never()).updateItem(fields, userId, itemId);
    }


    @SneakyThrows
    @Test
    void searchByNameOrDescriptionValid() {
        Long userId = 0L;
        mockMvc.perform(MockMvcRequestBuilders.get("/items/search")
                .param("text", "text")
                .header("x-sharer-user-id", userId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemService).searchByNameOrDescription("text");
    }

    @SneakyThrows
    @Test
    void addComment() {

        Long userId = 0L;
        Long itemId = 0L;
        CommentDto commentDtoCreate = CommentDto.builder().authorName("nick")
                .created(LocalDateTime.now()).text("text").build();


        Mockito.when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDtoCreate);

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/items/{itemId}/comment", itemId)
                .param("x-sharer-user-id", "0")
                .content(objectMapper.writeValueAsString(commentDtoCreate))
                .header("x-sharer-user-id", userId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(commentDtoCreate), result);
    }
}