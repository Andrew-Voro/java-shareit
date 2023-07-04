package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

@ExtendWith(MockitoExtension.class)
@DataJpaTest
class UserRepositoryTest {
    UserRepository userRepository;

    @BeforeEach
    private void addUsers() {
        userRepository.save(User.builder().email("a@n.com").name("an").build());
    }


    @AfterEach
    private void deleteUsers() {
        userRepository.deleteAll();
    }
}