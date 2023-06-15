package ru.practicum.shareit.item.repository;


import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> findByUserId(long userId);

    Item save(Item item);

    Item getItem(Long itemId);

    List<ItemDto> searchByNameOrDescription(String text);

}
