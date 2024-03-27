package ru.yandex.practicum.filmorate.ecxeption;

import java.util.NoSuchElementException;

public class NotFoundException extends NoSuchElementException {
    public NotFoundException(String message) {
        super(message);
    }
}
