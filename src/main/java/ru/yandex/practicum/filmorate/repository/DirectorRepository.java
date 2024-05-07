package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Component
@AllArgsConstructor
public class DirectorRepository {

    private JdbcTemplate template;

    public Director create(Director director) {
        template.update("insert into directors (name) values(?)",
                director.getName());
        Integer newId = 0;
        newId += template.queryForObject(
                "select max(id) as max from directors",
                (rs, rowNum) -> rs.getInt("max"));
        director.setId(newId);
        return director;
    }

    public Director update(Director director) {
        throwNotFoundExceptionForNonExistentId(director.getId());
        template.update(
                "update directors set name = ? where id = ?",
                director.getName(), director.getId());
        return director;
    }

    public Director getById(Integer id) {
        throwNotFoundExceptionForNonExistentId(id);
        return template.queryForObject(
                "select * from directors where id = ?",
                directorRowMapper(), id);
    }

    public List<Director> getAll() {
        return template.query(
                "select * from directors order by id asc", directorRowMapper());
    }

    public void removeById(Integer id) {
        throwNotFoundExceptionForNonExistentId(id);
        template.update("delete from directors_films where director_id = ?", id);
        template.update("delete from directors where id = ?", id);
    }

    private RowMapper<Director> directorRowMapper() {
        return (rs, rowNum) -> new Director()
                .setId(rs.getInt("id"))
                .setName(rs.getString("name"));
    }

    private void throwNotFoundExceptionForNonExistentId(int id) {
        String select = "select exists (select id from directors where id = ?) as match";
        if (Boolean.FALSE.equals(template.queryForObject(select,
                (rs, rowNum) -> rs.getBoolean("match"), id))) {
            throw new NotFoundException("No directors with such ID: " + id);
        }
    }
}
