package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
    public User create(@RequestBody User user) throws Exception {
        if (isValidUser(user)) {
            if (users.contains(user)) {
                log.info("User: {} is not saved, 'cause already exist", user);
                throw new IllegalArgumentException("UserAlreadyExistException");
            } else {
                int id = generateId();
                user.setId(id);
                users.add(user);
                log.info("User: {} is successfully saved", user);
            }
        }
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) throws Exception {
        if (isValidUser(user)) {
            if (!users.contains(user)) {
                log.info("User: {} is not updated, 'cause bad ID", user);
                throw new IllegalArgumentException("NoSuchUserException");
            } else {
                users.add(user);
                log.info("User: {} is successfully updated", user);
            }
        }
        return user;
    }

    @GetMapping
    public HashSet<User> findAll() {
        return users;
    }

    private boolean isValidUser(User user) throws Exception {
        if (user == null) {
            throw new IllegalArgumentException("NoArgumentException");
        }
        String login = user.getLogin();
        String email = user.getEmail();
        LocalDate birthday = user.getBirthday();
        if (login.isEmpty() || login.isBlank() || login.contains(" ")) {
            throw new IllegalArgumentException("IncorrectLoginException");
        }
        if (email.isBlank()
                || email.isEmpty()
                || !(email.contains("@") && email.contains("."))) {
            throw new IllegalArgumentException("IncorrectEmailException");
        }
        if (birthday.isAfter(LocalDate.now()) || birthday.equals(LocalDate.now())) {
            throw new IllegalArgumentException("IncorrectBirthdayException");
        }
        return true;
    }

}