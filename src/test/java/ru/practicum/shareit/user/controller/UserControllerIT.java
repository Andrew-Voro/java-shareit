package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
@SpringJUnitWebConfig({UserController.class})
class UserControllerIT {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private ObjectMapper objectMapper;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void saveNewUser() {
        UserDto userDtoCreate = new UserDto();
        userDtoCreate.setEmail("a@n.com");
        when(userService.saveUser(userDtoCreate)).thenReturn(userDtoCreate);
        String result = mockMvc.perform(post("/users/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDtoCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoCreate.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoCreate.getName())))
                .andExpect(jsonPath("$.email", is(userDtoCreate.getEmail())))                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(userDtoCreate), result);
    }

    @SneakyThrows
    @Test
    void getUserId() {
        long userId = 0L;
        mockMvc.perform(get("/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService).getUser(userId);
    }

    @SneakyThrows
    @Test
    void updateUserNotValid() {
        long userId = 0L;
        UserDto userDtoToUpdate = new UserDto();
        userDtoToUpdate.setEmail("a@n.com");
        userDtoToUpdate.setName(null);
        mockMvc.perform(patch("/users/{id}", userId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDtoToUpdate)))
                .andExpect(status().isBadRequest());
        verify(userService, never()).updateUser(userDtoToUpdate, userId);

    }

    @SneakyThrows
    @Test
    void updateUserValid() {
        long userId = 0L;
        UserDto userDtoToUpdate = new UserDto();
        userDtoToUpdate.setEmail("a@n.com");
        userDtoToUpdate.setName("an");
        mockMvc.perform(patch("/users/{id}", userId)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userDtoToUpdate)))
                .andExpect(status().isOk());
        verify(userService).updateUser(userDtoToUpdate, userId);

    }

    @SneakyThrows
    @Test
    void delete() {
        long userId = 0L;
        this.mockMvc.perform(MockMvcRequestBuilders
                .delete("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService).delete(userId);

    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        long userId = 0L;
        mockMvc.perform(get("/users/", userId))
                .andDo(print())
                .andExpect(status().isOk());
        verify(userService).getAllUsers();
    }
}