package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.RepeatException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UserStorageImpl implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    private int generateId() {
        return nextId++;
    }

    @Override
    public User create(User user) {
        if (users.containsKey(user.getId())) {
            log.info("User: {} is not saved, 'cause already exist", user);
            throw new RepeatException("User already exist");
        } else {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            int id = generateId();
            user.setId(id);
            users.put(id, user);
            log.info("User: {} is successfully saved", user);
        }
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            log.info("User: {} is not updated, 'cause bad ID", user);
            throw new NotFoundException("No User with such id");
        } else {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.replace(user.getId(), user);
            log.info("User: {} is successfully updated", user);
        }
        return user;
    }

    @Override
    public User getById(int id) {
        if (!users.containsKey(id)) {
            log.info("User doesn't be updated, 'cause It has wrong ID\n");
            throw new NotFoundException("No user with such id");
        }
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        log.info("Users are showed");
        return new ArrayList<>(users.values());
    }
}
