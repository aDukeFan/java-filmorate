package ru.yandex.practicum.filmorate.ecxeption;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
