package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest({ItemRequestController.class})

class ItemRequestControllerIT {
    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private ObjectMapper objectMapper;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @SneakyThrows
    @Test
    void add() {

        Long userId = 0L;

        ItemRequestDto itemRequestDtoAdd = ItemRequestDto.builder().created(LocalDateTime.now()).description("thing")
                .items(null).requestor(userId).build();
        Mockito.when(itemRequestService.add(anyLong(), any(ItemRequestDto.class))).thenReturn(itemRequestDtoAdd);

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                .param("x-sharer-user-id", "0")
                .content(objectMapper.writeValueAsString(itemRequestDtoAdd))
                .header("x-sharer-user-id", userId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemRequestDtoAdd), result);
    }
    @SneakyThrows
    @Test
    void get() {
        Long requestId = 0L;
        Long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", requestId)
                .param("x-sharer-user-id", "0")
                .header("x-sharer-user-id", userId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemRequestService).getRequest(userId, requestId);

    }
    @SneakyThrows
    @Test
    void getOwn() {
        Long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/")
                .param("x-sharer-user-id", "0")
                .header("x-sharer-user-id", userId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemRequestService).getOwn(userId);
    }
    @SneakyThrows
    @Test
    void getAllVerifyPagedRightCondition() {
        Long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                .param("x-sharer-user-id", "0")
                .param("from","1")
                .param("size","20")
                .header("x-sharer-user-id", userId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemRequestService).getAllPaged(userId, 1L, 20L);
    }



    @SneakyThrows
    @Test
    void getAllVerifyAll() {
        Long userId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                .param("x-sharer-user-id", "0")
                .header("x-sharer-user-id", userId)
                .contentType("application/json")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(itemRequestService).getAll(userId);
    }
}