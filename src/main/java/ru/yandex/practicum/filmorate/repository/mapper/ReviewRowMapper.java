package ru.yandex.practicum.filmorate.repository.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

@Component
public class ReviewRowMapper {

    public RowMapper<Review> getMapper() {
        return (rs, rowNum) -> new Review()
                .setReviewId(rs.getInt("id"))
                .setContent(rs.getString("content"))
                .setIsPositive(rs.getBoolean("is_positive"))
                .setFilmId(rs.getInt("film_id"))
                .setUserId(rs.getInt("user_id"))
                .setUseful(rs.getInt("useful"));
    }

}
