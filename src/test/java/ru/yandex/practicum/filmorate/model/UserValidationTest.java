package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldThrowIllegalArgumentExceptions() {
        User userWithWrongEmail = new User()
                .setLogin("thirdGay")
                .setEmail("yyayayaya")
                .setBirthday(LocalDate.of(2000, 1, 1));

        User userWithWrongBirthday = new User()
                .setLogin("forthGay")
                .setBirthday(LocalDate.now())
                .setEmail("forthGay@ya.ru");

        User userWithWrongLogin = new User()
                .setLogin("firth Gay")
                .setEmail("ohohoho@ya.ru")
                .setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violationsEmail = validator.validate(userWithWrongEmail);
        assertFalse(violationsEmail.isEmpty());
        Set<ConstraintViolation<User>> violationsBirthday = validator.validate(userWithWrongBirthday);
        assertFalse(violationsBirthday.isEmpty());
        Set<ConstraintViolation<User>> violationsLogin = validator.validate(userWithWrongLogin);
        assertFalse(violationsLogin.isEmpty());

//        assertThrows(IllegalArgumentException.class, () -> controller.create(userWithWrongBirthday));
//        assertThrows(IllegalArgumentException.class, () -> controller.create(userWithWrongEmail));
//        assertThrows(IllegalArgumentException.class, () -> controller.create(userWithWrongLogin));
//

    }

}