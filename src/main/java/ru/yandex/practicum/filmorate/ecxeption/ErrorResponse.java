package ru.yandex.practicum.filmorate.ecxeption;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    String error;
}