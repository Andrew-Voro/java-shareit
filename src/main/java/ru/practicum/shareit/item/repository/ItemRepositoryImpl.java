package ru.practicum.shareit.item.repository;

import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Data
@Getter
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> items = new HashMap<>();

    @Override
    public List<Item> findByUserId(long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Item getItem(Long itemId) {
        if (items.values().stream().flatMap(Collection::stream).map(Item::getId).collect(Collectors.toList()).contains(itemId)) {
            return items.values().stream().flatMap(Collection::stream).filter(x -> x.getId() == itemId).collect(Collectors.toList()).get(0);
        } else {
            throw new ObjectNotFoundException("getItem: предмета c id = " + itemId + " нет.");
        }
    }

    @Override
    public Item save(Item item) {
        item.setId(getId());
        items.compute(item.getOwner(), (userId, userItems) -> { //
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });

        return item;
    }

    private long getId() {
        long lastId = items.values()
                .stream()
                .flatMap(Collection::stream)
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }

    public List<ItemDto> searchByNameOrDescription(String text) {
        return items.values().stream().flatMap(Collection::stream)
                .filter(x -> (x.getName().toLowerCase().contains(text) || x.getDescription().toLowerCase().contains(text))
                        && x.getAvailable().booleanValue() == true)
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
