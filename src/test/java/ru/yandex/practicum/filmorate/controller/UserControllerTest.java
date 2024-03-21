package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {

    UserController controller = new UserController();

    @Test
    public void shouldCreateAndUpdateValidUsersById() {
        User userWithoutName = new User(
                "firstGay", "firstGay@mail.ru", LocalDate.of(2000, 1, 1));
        User userWithName = new User(
                "SecondGay", "SecondGay@ya.ru", LocalDate.of(2000, 1, 1));
        userWithName.setName("Billy");

        User savedUserWithoutName = controller.create(userWithoutName);
        assertEquals(savedUserWithoutName.getName(), "firstGay");
        assertEquals(savedUserWithoutName.getLogin(), "firstGay");
        assertEquals(savedUserWithoutName.getEmail(), "firstGay@mail.ru");
        assertEquals(savedUserWithoutName.getBirthday(), LocalDate.of(2000, 1, 1));
        assertEquals(controller.findAll().size(), 1);

        User savedUserWithName = controller.create(userWithName);
        assertEquals(savedUserWithName.getName(), "Billy");
        assertEquals(savedUserWithName.getLogin(), "SecondGay");
        assertEquals(savedUserWithName.getEmail(), "SecondGay@ya.ru");
        assertEquals(savedUserWithName.getBirthday(), LocalDate.of(2000, 1, 1));
        assertEquals(controller.findAll().size(), 2);

        User userToUpdate = new User(
                "firstGay_2", "firstGay@mail.ru", LocalDate.of(2000, 1, 1));
        userToUpdate.setName("Bobby");
        userToUpdate.setId(2);
        User updatedUser = controller.update(userToUpdate);
        assertEquals(updatedUser.getName(), "Bobby");
        assertEquals(updatedUser.getLogin(), "firstGay_2");
        assertEquals(updatedUser.getEmail(), "firstGay@mail.ru");
        assertEquals(updatedUser.getBirthday(), LocalDate.of(2000, 1, 1));
        assertEquals(controller.findAll().size(), 2);
    }

    @Test
    public void shouldSetSameNameAsLoginToUserWithoutName() {
        User userWithoutName = new User(
                "firstGay", "firstGay@mail.ru", LocalDate.of(2000, 1, 1));
        User createdUser = controller.create(userWithoutName);
        assertEquals(createdUser.getName(), createdUser.getLogin());
    }
}