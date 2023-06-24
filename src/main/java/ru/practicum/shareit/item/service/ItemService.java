package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {
    List<ItemDto> getItems(Long userId);

    ItemDto addNewItem(Long userId, ItemDto item);

    ItemDto updateItem(Map<String, Object> fields, Long userId, Long itemId);

    List<ItemDto> searchByNameOrDescription(String text);
}
