package ru.yandex.practicum.filmorate.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

@Component
public class RatingRowMapper {

    public RowMapper<Rating> getMapper() {
        return (rs, rowNum) -> new Rating()
                .setId(rs.getInt("id"))
                .setName(rs.getString("name"));
    }
}
