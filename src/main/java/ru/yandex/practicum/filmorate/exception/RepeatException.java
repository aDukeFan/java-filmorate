package ru.yandex.practicum.filmorate.exception;

public class RepeatException extends IllegalArgumentException {
    public RepeatException(String message) {
        super(message);
    }
}
