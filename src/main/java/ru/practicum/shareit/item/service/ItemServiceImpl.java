package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final  ItemRepository repository;


    @Override
    public List<ItemDto> getItems(Long userId) {
        List<Item> userItems = repository.findByUserId(userId);
        return ItemMapper.toItemDto(userItems);
    }


    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        Item item = repository.save(ItemMapper.toDtoItem(itemDto, userId));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Map<String, Object> fields, Long userId, Long itemId) {


        Item item = repository.findByUserId(userId).stream().filter(x -> x.getId().equals(itemId)).collect(Collectors.toList()).get(0);

        fields.forEach((k, v) -> {
            Field field = ReflectionUtils.findField(Item.class, k);
            field.setAccessible(true);
            if (v instanceof Integer) {
                Long w = ((Integer) v).longValue();
                ReflectionUtils.setField(field, item, w);
            } else {
                ReflectionUtils.setField(field, item, v);
            }
        });
        item.setOwner(userId);
        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> searchByNameOrDescription(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        text = text.toLowerCase();
        return repository.searchByNameOrDescription(text);
    }
}
