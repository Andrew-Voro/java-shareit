package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Map;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    @PostMapping
    public ResponseEntity<ItemRequestDto> add(@RequestHeader Map<String, String> headers,
                                              @RequestHeader("X-Sharer-User-Id") long userId,
                                              @RequestBody ItemRequestDto itemRequestDto) {
        if (!headers.containsKey("x-sharer-user-id")) {
            log.info("Нет заголовка: X-Sharer-User-Id.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


    return new  ResponseEntity<>(itemRequestService.add(userId,itemRequestDto), HttpStatus.OK);
    }

}
