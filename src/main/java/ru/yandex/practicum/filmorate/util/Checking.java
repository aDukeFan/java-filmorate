package ru.yandex.practicum.filmorate.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Component
public class Checking {
    private final JdbcTemplate template;

    public Checking(JdbcTemplate template) {
        this.template = template;
    }

    public void exist(int id, String table) {
        String select = "select id from " + table + " where id = ?";
        if (!template.queryForRowSet(select, id).next()) {
            throw new NotFoundException("No " + table + " with such ID: " + id);
        }
    }

    public void valid(int id, String table) {
        String select = "select id from " + table + " where id = ?";
        if (!template.queryForRowSet(select, id).next()) {
            throw new ValidationException("No " + table + " with such ID: " + id);
        }
    }

    public boolean isFriends(int userId, int friendId) {
        return Boolean.TRUE.equals(template.queryForObject(
                "select exists (select * from follows where following_id = ? and followed_id = ?) as match",
                (rs, rowNum) -> rs.getBoolean("match"), friendId, userId));
    }

    public boolean isFilmWithRating(Integer id) {
        return Boolean.TRUE.equals(template.queryForObject(
                "select exists (select rating_id from films where rating_id is not null and id = ?) as match",
                (rs, rowNum) -> rs.getBoolean("match"), id));
    }
}
