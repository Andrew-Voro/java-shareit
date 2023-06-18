package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.handler.exception.ObjectNotFoundException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryUserStorage /*implements UserRepository*/ {
    private static long idCounter;
    private final Map<Long, User> users = new HashMap<>();

   // @Override
    public User findUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("getUser: Юзера c id = " + id + " нет.");
        }
        return users.get(id);
    }

   // @Override
    public User create(User user) {

        if (!users.values().stream().map(User::hashCode).collect(Collectors.toList()).contains(user.hashCode())) {
            user.setId(++idCounter);
            users.put(user.getId(), user);
            log.debug("POST: Пользователь {} с электронной почтой {} зарегистрирован. ", user.getName(),
                    user.getEmail());
        } else {
            log.debug("POST: ValidationException пользователь {} с электронной почтой {} ранее зарегистрирован. ",
                    user.getName(), user.getEmail());
            throw new ValidationException("Пользователь " + user.getName() + " с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        }
        return user;
    }


    //@Override
    public Collection<User> findAll() {
        return users.values();
    }

    //@Override
    public void delete(Long id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new ValidationException("Delete: ValidationException пользователь c  id = " + id +
                    " отсутствует в базе. ");
        }
    }

    //@Override
    public Map<Long, User> getUsers() {
        return users;
    }


}
