package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserRepository {
    User create(User user);

    Collection<User> findAll();

    User findUserById(Long id); //new

    void delete(Long id);

    Map<Long, User> getUsers();
}
