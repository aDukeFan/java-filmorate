package ru.yandex.practicum.filmorate.util.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

@Component
public class DirectorRowMapper {

    public RowMapper<Director> mapper() {
        return (rs, rowNum) -> new Director()
                .setId(rs.getInt("id"))
                .setName(rs.getString("name"));
    }
}
