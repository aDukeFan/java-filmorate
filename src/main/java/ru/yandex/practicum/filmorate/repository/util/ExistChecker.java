package ru.yandex.practicum.filmorate.repository.util;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Component
@AllArgsConstructor
public class ExistChecker {

    private final JdbcTemplate template;

    public void throwNotFountException(int id, String table) {
        String select = "select id from " + table + " where id = ?";
        if (!template.queryForRowSet(select, id).next()) {
            throw new NotFoundException("No " + table + " with such ID: " + id);
        }
    }

    public void throwValidationException(int id, String table) {
        String select = "select id from " + table + " where id = ?";
        if (!template.queryForRowSet(select, id).next()) {
            throw new ValidationException("No " + table + " with such ID: " + id);
        }
    }
}
