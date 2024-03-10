package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    UserController controller = new UserController();

    @Test
    public void shouldCreateAndUpdateValidUsersById() throws Exception {
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
    public void shouldThrowIllegalArgumentExceptions() throws Exception {
        User userWithWrongEmail = new User(
                "thirdGay", "yyayayaya", LocalDate.of(2000, 1, 1));
        User userWithWrongBirthday = new User(
                "forthGay", "forthGay@ya.ru", LocalDate.now());
        User userWithWrongLogin = new User(
                "firth Gay", "ohohoho@ya.ru", LocalDate.of(2000, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> controller.create(userWithWrongBirthday));
        assertThrows(IllegalArgumentException.class, () -> controller.create(userWithWrongEmail));
        assertThrows(IllegalArgumentException.class, () -> controller.create(userWithWrongLogin));

        User userWithoutName = new User(
                "firstGay", "firstGay@mail.ru", LocalDate.of(2000, 1, 1));
        controller.create(userWithoutName);

        User userWithoutNameToUpdate = new User(
                "firstGay", "firstGay@mail.ru", LocalDate.of(2002, 1, 1));
        userWithoutNameToUpdate.setId(2);
        assertThrows(IllegalArgumentException.class, () -> controller.update(userWithoutNameToUpdate));
    }


}