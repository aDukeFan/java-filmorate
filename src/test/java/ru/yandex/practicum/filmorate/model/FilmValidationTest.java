package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

class FilmValidationTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldNotPassValidation() {
        //дата релиза — не раньше 28 декабря 1895 года;
        Film filmWithWrongDate = new Film()
                .setName("Public Enemies")
                .setDescription("about public enemies")
                .setReleaseDate(LocalDate.of(1895, 12, 28))
                .setDuration(200);
        Set<ConstraintViolation<Film>> violationsRelease = validator.validate(filmWithWrongDate);
        assertFalse(violationsRelease.isEmpty());
        //название не может быть пустым;
        Film filmWithWrongName = new Film()
                .setDescription("about gangsters")
                .setReleaseDate(LocalDate.of(1999, 12, 28))
                .setDuration(200);
        Set<ConstraintViolation<Film>> violationsName = validator.validate(filmWithWrongName);
        assertFalse(violationsName.isEmpty());
        //продолжительность фильма должна быть положительной.
        Film filmWithWrongDuration = new Film()
                .setName("Public Enemies")
                .setDescription("about public enemies")
                .setReleaseDate(LocalDate.of(1999, 12, 28))
                .setDuration(-200);
        Set<ConstraintViolation<Film>> violationsDuration = validator.validate(filmWithWrongDuration);
        assertFalse(violationsDuration.isEmpty());
        //максимальная длина описания — 200 символов;
        String description = "i".repeat(201);

        Film filmWithWrongDescription = new Film()
                .setName("Public Enemies")
                .setDescription(description)
                .setReleaseDate(LocalDate.of(1985, 12, 28))
                .setDuration(200);
        Set<ConstraintViolation<Film>> violationsDescription = validator.validate(filmWithWrongDescription);
        assertFalse(violationsDescription.isEmpty());
    }
}