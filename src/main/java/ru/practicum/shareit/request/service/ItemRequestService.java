package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto add(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getRequest(Long userId, Long requestId);

    List<ItemRequestDto> getOwn(Long userId);

    List<ItemRequestDto> getAllPaged(Long userId, Long from, Long size);

    List<ItemRequestDto> getAll(Long userId);
}
