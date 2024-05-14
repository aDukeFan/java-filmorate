package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.util.Checking;
import ru.yandex.practicum.filmorate.util.mappers.RatingRowMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class RatingRepository {

    private JdbcTemplate template;
    private RatingRowMapper ratingRowMapper;
    private Checking checking;

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
        checking.exist(rating.getId(), "ratings");
        template.update(
                "update ratings set name = ? where id = ?",
                rating.getName(), rating.getId());
        return rating;
    }

    public Rating getById(int ratingId) {
        checking.exist(ratingId, "ratings");
        return template.queryForObject(
                "select * from ratings where id = ?", ratingRowMapper.mapper(),
                ratingId);
    }

    public List<Rating> getAll() {
        return template.query("select * from ratings order by id", ratingRowMapper.mapper());
    }

    public void removeById(int ratingId) {
        checking.exist(ratingId, "ratings");
        template.update("delete from ratings where id = ?", ratingId);
    }
}
