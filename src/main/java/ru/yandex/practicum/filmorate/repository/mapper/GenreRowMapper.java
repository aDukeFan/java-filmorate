package ru.yandex.practicum.filmorate.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

@Component
public class GenreRowMapper {

    public RowMapper<Genre> getMapper() {
        return (rs, rowNum) -> new Genre()
                .setId(rs.getInt("id"))
                .setName(rs.getString("name"));
    }
}
