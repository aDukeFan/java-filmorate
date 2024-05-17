package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.util.ExistChecker;
import ru.yandex.practicum.filmorate.repository.mapper.GenreRowMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class GenreRepository {

    private JdbcTemplate template;
    private GenreRowMapper genreRowMapper;
    private ExistChecker existChecker;

    public Genre create(Genre genre) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    "insert into genres (name) values(?)",
                    new String[]{"id"});
            stmt.setString(1, genre.getName());
            return stmt;
        }, keyHolder);
        return genre.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    public Genre update(Genre genre) {
        existChecker.throwNotFountException(genre.getId(), "genres");
        template.update(
                "update genres set name = ? where id = ?",
                genre.getName(), genre.getId());
        return genre;
    }

    public Genre getById(int genreId) {
        existChecker.throwNotFountException(genreId, "genres");
        return template.queryForObject(
                "select * from genres where id = ?", genreRowMapper.getMapper(),
                genreId);
    }

    public List<Genre> getAll() {
        return template.query("select * from genres order by id asc", genreRowMapper.getMapper());
    }

    public void removeById(int genreId) {
        existChecker.throwNotFountException(genreId, "genres");
        template.update("delete from genres where id = ?", genreId);
    }
}
