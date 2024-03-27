package ru.yandex.practicum.filmorate.ecxeption;

import java.util.NoSuchElementException;

public class IncorrectCountException extends NoSuchElementException {
    public IncorrectCountException(String message) {
        super(message);
    }
}
