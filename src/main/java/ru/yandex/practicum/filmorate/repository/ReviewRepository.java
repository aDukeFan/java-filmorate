package ru.yandex.practicum.filmorate.repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.util.ExistChecker;
import ru.yandex.practicum.filmorate.repository.mapper.ReviewRowMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
public class ReviewRepository {

    private JdbcTemplate template;
    private ReviewRowMapper reviewMapper;
    private ExistChecker existChecker;
    private EventRepository events;

    public Review create(Review review) {
        log.info("на сохранение получен отзыв с userId: {} filmId: {}",
                review.getUserId(), review.getFilmId());

        existChecker.throwNotFountException(review.getUserId(), "users");
        existChecker.throwNotFountException(review.getFilmId(), "films");

        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO reviews(content, is_positive, user_id, film_id) VALUES(?,?,?,?)",
                    new String[]{"id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setInt(3, review.getUserId());
            stmt.setInt(4, review.getFilmId());
            return stmt;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        log.info("Создан отзыв с id {}", review.getReviewId());
        events.addEvent(review.getUserId(), "REVIEW", review.getReviewId(), "ADD");
        return review;
    }

    public Review update(Review review) {
        Integer id = review.getReviewId();
        existChecker.throwNotFountException(id, "reviews");
        Integer authorId = template.queryForObject(
                "select user_id from reviews where id = ?",
                Integer.class,
                id);
        template.update("UPDATE reviews " +
                        "SET content = ?, is_positive = ? " +
                        "WHERE id = ?",
                review.getContent(), review.getIsPositive(), review.getReviewId());
        log.info("Обновлен отзыв с id {}", review.getReviewId());
        events.addEvent(authorId, "REVIEW", id, "UPDATE");
        return getReviewById(review.getReviewId());
    }

    public void delete(Integer id) {
        existChecker.throwNotFountException(id, "reviews");
        Integer authorId = template.queryForObject(
                "select user_id from reviews where id = ?",
                Integer.class, id);

        template.update("delete from reviews where id = ?", id);
        log.info("Удален отзыв с id {}", id);
        events.addEvent(authorId, "REVIEW", id, "REMOVE");
    }

    public Review getReviewById(Integer id) {
        existChecker.throwNotFountException(id, "reviews");
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
                reviewMapper.getMapper(), id);
    }

    public List<Review> getAllReviewsByFilmIdAndCount(Integer filmId, Integer count) {
        existChecker.throwNotFountException(filmId, "films");
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
                reviewMapper.getMapper(), filmId, count);
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
                reviewMapper.getMapper(), count);
    }

    public void addLikeReview(Integer id, Integer userId) {
        template.update(
                "INSERT INTO reviews_rates (review_id, user_id, useful) VALUES (?, ?, ?)",
                id, userId, 1);
    }

    public void addDisLikeToReview(Integer id, Integer userId) {
        template.update(
                "INSERT INTO reviews_rates(review_id, user_id, useful) VALUES (?, ?, ?)",
                id, userId, -1);
    }

    public void deleteLikeReview(Integer id, Integer userId) {
        template.update(
                "DELETE FROM reviews_rates WHERE review_id = ? AND user_id = ? AND useful = 1",
                id, userId);
    }

    public void deleteDisLikeFromReview(Integer id, Integer userId) {
        template.update(
                "DELETE FROM reviews_rates WHERE review_id = ? AND user_id = ? AND useful = -1",
                id, userId);
    }
}
