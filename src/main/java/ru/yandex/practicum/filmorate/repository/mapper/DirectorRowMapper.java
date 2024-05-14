package ru.yandex.practicum.filmorate.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

@Component
public class DirectorRowMapper {

    public RowMapper<Director> getMapper() {
        return (rs, rowNum) -> new Director()
                .setId(rs.getInt("id"))
                .setName(rs.getString("name"));
    }
}
