package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
@AllArgsConstructor
public class GenreRepository {

    private JdbcTemplate template;

    public Genre create(Genre genre) {
        template.update(
                "insert into genres (name) values(?)",
                genre.getName());
        return template.queryForObject(
                "select * from genres where name = ?",
                genreRowMapper(), genre.getName());
    }

    public Genre update(Genre genre) {
        throwNotFoundExceptionForNonExistentId(genre.getId());
        template.update(
                "update genres set name = ? where id = ?",
                genre.getName(), genre.getId());
        return genre;
    }

    public Genre getById(int genreId) {
        throwNotFoundExceptionForNonExistentId(genreId);
        return template.queryForObject(
                "select * from genres where id = ?",
                genreRowMapper(), genreId);
    }

    public List<Genre> getAll() {
        return template.query(
                "select * from genres order by id asc", genreRowMapper());
    }

    public void removeById(int genreId) {
        throwNotFoundExceptionForNonExistentId(genreId);
        template.update("delete from genres where id = ?", genreId);
    }

    private RowMapper<Genre> genreRowMapper() {
        return (rs, rowNum) -> new Genre()
                .setId(rs.getInt("id"))
                .setName(rs.getString("name"));
    }

    private void throwNotFoundExceptionForNonExistentId(int id) {
        String select = "select exists (select id from genres where id = ?) as match";
        if (Boolean.FALSE.equals(template.queryForObject(select,
                (rs, rowNum) -> rs.getBoolean("match"), id))) {
            throw new NotFoundException("No users with such ID: " + id);
        }
    }
}
