package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;


    @BeforeEach
    private void addItems() {

        User owner = userRepository.save(User.builder().email("a@n.com").name("an").build());
        User user = userRepository.save(User.builder().email("ai@n.com").name("ain").build());
        ItemRequest request = itemRequestRepository.save(ItemRequest.builder().created(now()).description("thing").items(new ArrayList<>())
                .requestor(user).build());
        itemRepository.save(Item.builder().requestId(request.getId()).available(true).description("thing").name("thing")
                .owner(owner).build());
    }

    @Test
    void findByOwnerOrderById() {

        List<Item> items = itemRepository.findByOwnerOrderById(1L);
        assertEquals(items.size(), 1);
    }

    @Test
    void searchByNameOrDescription() {
        List<Item> items = itemRepository.searchByNameOrDescription("thing");
        assertEquals(items.size(), 1);
        assertEquals(items.get(0).getName(), "thing");
    }

    @Test
    void findByRequestId() {
        List<Item> items = itemRepository.findByRequestId(1L);
        assertEquals(items.size(), 1);//0
        assertEquals(items.get(0).getRequestId(), 1L);
    }


    @AfterEach
    private void deleteItems() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();

    }

}