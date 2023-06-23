package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Map;

public interface ItemService {

    List<ItemDto> getItems(Long userId);

    ItemDto addNewItem(Long userId, ItemDto item);

    ItemDto getItem(Long itemId,Long userId);

    ItemDto updateItem(Map<String, Object> fields, Long userId, Long itemId);

    List<ItemDto> searchByNameOrDescription(String text);
    CommentDto addComment(Long itemId,Long userId,CommentDto commentDto);
}
