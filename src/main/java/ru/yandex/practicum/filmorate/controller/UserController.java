package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.HashSet;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final HashSet<User> users = new HashSet<>();

    private int nextId = 1;

    private int generateId() {
        return nextId++;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (users.contains(user)) {
            log.info("User: {} is not saved, 'cause already exist", user);
            throw new IllegalArgumentException("UserAlreadyExistException");
        } else {
            int id = generateId();
            user.setId(id);
            users.add(user);
            log.info("User: {} is successfully saved", user);
        }
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (!users.contains(user)) {
            log.info("User: {} is not updated, 'cause bad ID", user);
            throw new IllegalArgumentException("NoSuchUserException");
        } else {
            users.add(user);
            log.info("User: {} is successfully updated", user);
        }
        return user;
    }

    @GetMapping
    public HashSet<User> findAll() {
        return users;
    }
}