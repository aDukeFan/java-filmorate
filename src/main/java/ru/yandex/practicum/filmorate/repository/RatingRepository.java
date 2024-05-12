package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.util.mappers.RatingRowMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class RatingRepository {

    private JdbcTemplate template;
    private RatingRowMapper ratingRowMapper;

    public Rating create(Rating rating) {

        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    "insert into ratings (name) values(?)",
                    new String[]{"id"});
            stmt.setString(1, rating.getName());
            return stmt;
        }, keyHolder);
        return rating.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    public Rating update(Rating rating) {
        throwNotFoundExceptionForNonExistentId(rating.getId());
        template.update(
                "update ratings set name = ? where id = ?",
                rating.getName(),
                rating.getId());
        return rating;
    }

    public Rating getById(int ratingId) {
        throwNotFoundExceptionForNonExistentId(ratingId);
        return template.queryForObject(
                "select * from ratings where id = ?",
                ratingRowMapper.mapper(),
                ratingId);
    }

    public List<Rating> getAll() {
        return template.query(
                "select * from ratings order by id",
                ratingRowMapper.mapper());
    }

    public void removeById(int ratingId) {
        throwNotFoundExceptionForNonExistentId(ratingId);
        template.update(
                "delete from ratings where id = ?",
                ratingId);

    }

    private void throwNotFoundExceptionForNonExistentId(int id) {
        String select = "select exists (select id from ratings where id = ?) as match";
        if (Boolean.FALSE.equals(template.queryForObject(select,
                (rs, rowNum) -> rs.getBoolean("match"), id))) {
            throw new NotFoundException("No users with such ID: " + id);
        }
    }

}
