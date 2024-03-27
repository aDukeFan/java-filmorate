package ru.yandex.practicum.filmorate.ecxeption;

public class RepeatException extends IllegalArgumentException {
    public RepeatException(String message) {
        super(message);
    }
}
