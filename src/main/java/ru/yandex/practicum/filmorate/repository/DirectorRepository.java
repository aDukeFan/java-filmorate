package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.util.Checking;
import ru.yandex.practicum.filmorate.util.mappers.DirectorRowMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
@Slf4j
public class DirectorRepository {

    private JdbcTemplate template;
    private DirectorRowMapper directorRowMapper;
    private Checking checking;

    public Director create(Director director) {
        log.info("Директор с именем: {} - получен на сохранение", director.getName());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    "insert into directors (name) values(?)",
                    new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        log.info("Директор с именем: {} - получил id {} и сохранен",
                director.getName(), director.getId());
        return director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    public Director update(Director director) {
        checking.exist(director.getId(), "directors");
        template.update(
                "update directors set name = ? where id = ?",
                director.getName(), director.getId());
        log.info("Директор с id {} получил имя {}", director.getId(), director.getName());
        return director;
    }

    public Director getById(Integer id) {
        checking.exist(id, "directors");
        return template.queryForObject(
                "select * from directors where id = ?",
                directorRowMapper.mapper(), id);
    }

    public List<Director> getAll() {
        return template.query("select * from directors order by id asc",
                directorRowMapper.mapper());
    }

    public void removeById(Integer id) {
        checking.exist(id, "directors");
        template.update("delete from directors where id = ?", id);
        log.info("Директор с id {} удален", id);
    }

}
