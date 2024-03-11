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
        Film filmWithWrongDate = new Film(
                "Public Enemies", "about gangsters",
                LocalDate.of(1985, 12, 27), 200);
        Set<ConstraintViolation<Film>> violationsRelease = validator.validate(filmWithWrongDate);
        assertFalse(violationsRelease.isEmpty());
        //название не может быть пустым;
        Film filmWithWrongName = new Film(
                "", "about gangsters",
                LocalDate.of(1999, 12, 28), 200);
        Set<ConstraintViolation<Film>> violationsName = validator.validate(filmWithWrongName);
        assertFalse(violationsName.isEmpty());
        //продолжительность фильма должна быть положительной.
        Film filmWithWrongDuration = new Film(
                "Public Enemies", "about gangsters",
                LocalDate.of(1999, 12, 28), -200);
        Set<ConstraintViolation<Film>> violationsDuration = validator.validate(filmWithWrongDuration);
        assertFalse(violationsDuration.isEmpty());
        //максимальная длина описания — 200 символов;
        String description = "i".repeat(201);
        Film filmWithWrongDescription = new Film(
                "Public Enemies", description,
                LocalDate.of(1999, 12, 28), 200);
        Set<ConstraintViolation<Film>> violationsDescription = validator.validate(filmWithWrongDescription);
        assertFalse(violationsDescription.isEmpty());
    }

    @Test
    public void shouldNotPassValidationCauseBadReleaseDate() {
        // Специально написал этот тест, создав объект идентичный тому,
        // который не проходит автотест в Постмане, долго искал ошибку.
        // Если тест запускать отдельно, то программа работает должным образом,
        // не понимаю в чем проблема, к сожалению.
        String name = "j3BmkOApqE9jJy2";
        String description = "sQarY3hPTHfuQ0NdFlqCMX3tNYSpFW86NUqYhWuJoJ2SZPJ6A5";
        long duration = 176;
        LocalDate releaseDate = LocalDate.of(1978, 4, 9);
        Film film = new Film(name, description, releaseDate, duration);
        Set<ConstraintViolation<Film>> violationsDescription = validator.validate(film);
        assertFalse(violationsDescription.isEmpty());
    }
}