package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ReviewRepository {

    private JdbcTemplate template;

    public Review create(Review review) {
        log.info("на сохранение получен отзыв с userId: {} filmId: {}",
                review.getUserId(),
                review.getFilmId());
        if (!doesObjectExist(review.getUserId(), "users")) {
            throw new NotFoundException("No users with such id");
        }
        if (!doesObjectExist(review.getFilmId(), "films")) {
            throw new NotFoundException("No film with such id");
        }

        String sql = "INSERT INTO reviews(content, is_positive, user_id, film_id) VALUES(?,?,?,?)";
        template.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId());
        String sqlForGettingId = "select max(id) as last from reviews";
        Integer reviewId = template.queryForObject(sqlForGettingId, Integer.class);
        review.setReviewId(reviewId);
        log.info("Создан отзыв с id {}", review.getReviewId());
        addEvent(review.getUserId(), "REVIEW", review.getReviewId(), "ADD");
        return review;
    }

    public Review update(Review review) {
        Integer id = review.getReviewId();
        if (doesObjectExist(id, "reviews")) {
            Integer authorId = template.queryForObject(
                    "select user_id from reviews where id = ?",
                    Integer.class, id);
            template.update("UPDATE reviews " +
                            "SET content = ?, is_positive = ? " +
                            "WHERE id = ?",
                    review.getContent(),
                    review.getIsPositive(),
                    review.getReviewId());
            log.info("Обновлен отзыв с id {}", review.getReviewId());
            addEvent(authorId, "REVIEW", id, "UPDATE");
        }
        return getReviewById(review.getReviewId());
    }

    public void delete(Integer id) {
        if (doesObjectExist(id, "reviews")) {
            Integer authorId = template.queryForObject(
                    "select user_id from reviews where id = ?",
                    Integer.class, id);
            String sql = "delete from reviews where id = ?";
            template.update(sql, id);
            log.info("Удален отзыв с id {}", id);
            addEvent(authorId, "REVIEW", id, "REMOVE");
        }
    }

    public Review getReviewById(Integer id) {
        if (!doesObjectExist(id, "reviews")) {
            throw new NotFoundException("No reviews with such ID: " + id);
        }
        return template.queryForObject("SELECT " +
                        "r.id AS id, " +
                        "r.content AS content, " +
                        "r.is_positive AS is_positive, " +
                        "r.user_id AS user_id, " +
                        "r.film_id AS film_id, " +
                        "sum(rr.useful) AS useful " +
                        "FROM reviews AS r " +
                        "LEFT JOIN reviews_rates AS rr ON rr.review_id = r.id " +
                        "WHERE r.id = ?",
                getReviewRowMapper(), id);
    }

    public List<Review> getAllReviewsByFilmIdAndCount(Integer filmId, Integer count) {
        if (!doesObjectExist(filmId, "films")) {
            throw new NotFoundException("No film with such id");
        }
        return template.query("SELECT " +
                        "r.id AS id, " +
                        "r.content AS content, " +
                        "r.is_positive AS is_positive, " +
                        "r.user_id AS user_id, " +
                        "r.film_id AS film_id, " +
                        "sum(rr.useful) AS useful " +
                        "FROM reviews AS r " +
                        "LEFT JOIN reviews_rates AS rr ON rr.review_id = r.id " +
                        "WHERE r.film_id = ? " +
                        "GROUP BY id " +
                        "ORDER BY useful DESC " +
                        "LIMIT (?)",
                getReviewRowMapper(), filmId, count);
    }

    public List<Review> getAllReviewsByCount(int count) {
        return template.query("SELECT " +
                        "r.id AS id, " +
                        "r.content AS content, " +
                        "r.is_positive AS is_positive, " +
                        "r.user_id AS user_id, " +
                        "r.film_id AS film_id, " +
                        "sum(rr.useful) AS useful " +
                        "FROM reviews AS r " +
                        "LEFT JOIN reviews_rates AS rr ON rr.review_id = r.id " +
                        "GROUP BY id " +
                        "LIMIT (?)",
                getReviewRowMapper(), count);
    }

    public void addLikeReview(Integer id, Integer userId) {
        String sql = "INSERT INTO reviews_rates (review_id, user_id, useful) VALUES (?, ?, ?)";
        template.update(sql, id, userId, 1);
    }

    public void addDisLikeToReview(Integer id, Integer userId) {
        String sql = "INSERT INTO reviews_rates(review_id, user_id, useful) VALUES (?, ?, ?)";
        template.update(sql, id, userId, -1);
    }

    public void deleteLikeReview(Integer id, Integer userId) {
        String sql = "DELETE FROM reviews_rates WHERE review_id = ? AND user_id = ? AND useful = 1";
        template.update(sql, id, userId);
    }

    public void deleteDisLikeFromReview(Integer id, Integer userId) {
        String sql = "DELETE FROM reviews_rates WHERE review_id = ? AND user_id = ? AND useful = -1";
        template.update(sql, id, userId);
    }

    private void addEvent(Integer userId, String eventType, Integer entityId, String operation) {
        template.update("INSERT INTO events " +
                        "(user_id, event_type, entity_id, operation, event_timestamp) " +
                        "VALUES(?,?,?,?,CURRENT_TIMESTAMP)",
                userId, eventType, entityId, operation);
    }

    private Boolean doesObjectExist(Integer id, String tableName) {
        String select = "SELECT EXISTS (SELECT id FROM " + tableName + " WHERE id = ?) AS match";
        return Boolean.TRUE.equals(template.queryForObject(select,
                (rs, rowNum) -> rs.getBoolean("match"), id));
    }

    private RowMapper<Review> getReviewRowMapper() {
        return (rs, rowNum) -> Review.builder()
                .reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .filmId(rs.getInt("film_id"))
                .userId(rs.getInt("user_id"))
                .useful(rs.getInt("useful"))
                .build();
    }
}
