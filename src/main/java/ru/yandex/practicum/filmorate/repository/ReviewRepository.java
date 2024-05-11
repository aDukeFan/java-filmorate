package ru.yandex.practicum.filmorate.repository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Component
@AllArgsConstructor
public class ReviewRepository {

    private JdbcTemplate template;

    public Review create(Review review) {
        //useful не должно быть в таблице reviews
        log.info("на сохранение получен отзыв с userId: {} filmId: {}",
                review.getUserId(), review.getFilmId());
        throwNotFoundExceptionForNonExistentId(review.getUserId(), "users");
        throwNotFoundExceptionForNonExistentId(review.getFilmId(), "films");
        String sql = "insert into reviews(content, is_positive, user_id, film_id) values(?,?,?,?)";
        template.update(sql, review.getContent(), review.getIsPositive(),
                review.getUserId(), review.getFilmId());
        String sqlForGettingId = "select max(id) as last from reviews";
        Integer reviewId = template.queryForObject(sqlForGettingId, Integer.class);
        review.setReviewId(reviewId);
        log.info("Создан отзыв с id {}", review.getReviewId());
        return review; // почему-то не создатеся review с id = 2, после первого сразу третий идет (?)
    }

    public Review update(Review review) {
        String sql = "update reviews set content = ?, is_positive = ?, user_id = ?, film_id = ? where id = ?";
        template.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getReviewId());
        log.info("Обновлен отзыв с id {}", review.getReviewId());
        review.setUseful(getReviewRate(review.getReviewId()));
        return review; // в тесте film_id и user_id увеличиваются после обновления (?)
    }

    public void delete(Integer id) {
        String sql = "delete from reviews where id = ?";
        template.update(sql, id);
        log.info("Удален отзыв с id {}", id);
    }

    public Review getReviewById(Integer id) {
        throwNotFoundExceptionForNonExistentId(id, "reviews");
        String sql = "select * from reviews where id = ?";
        SqlRowSet sqlRowSet = template.queryForRowSet(sql, id);
        while (sqlRowSet.next()) {
            Review review = Review.builder().build();
            review.setReviewId(sqlRowSet.getInt("id"));
            review.setContent(sqlRowSet.getString("content"));
            review.setIsPositive(sqlRowSet.getBoolean("is_positive"));
            review.setUserId(sqlRowSet.getInt("user_id"));
            review.setFilmId(sqlRowSet.getInt("film_id"));
            review.setUseful(this.getReviewRate(review.getReviewId()));
            return review;
        }
        return null;
    }

    public List<Review> getAllReviewsByFilmId(Integer filmId, Integer count) {
        if (filmId == -1) {
            return getAllReviewsByCount(count);
        } else {
            throwNotFoundExceptionForNonExistentId(filmId, "films");
            return getAllReviewsByFilmIdAndCount(filmId, count);
        }
    }

    private List<Review> getAllReviewsByFilmIdAndCount(Integer filmId, Integer count) {
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
                (rs, rowNum) -> Review.builder()
                .reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .filmId(rs.getInt("film_id"))
                .userId(rs.getInt("user_id"))
                .useful(rs.getInt("useful"))
                .build(), filmId, count);
    }



    private List<Review> getAllReviewsByCount(int count) {
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
                        "ORDER BY useful DESC " +
                        "LIMIT (?)",
                (rs, rowNum) -> Review.builder()
                        .reviewId(rs.getInt("id"))
                        .content(rs.getString("content"))
                        .isPositive(rs.getBoolean("is_positive"))
                        .filmId(rs.getInt("film_id"))
                        .userId(rs.getInt("user_id"))
                        .useful(rs.getInt("useful"))
                        .build(), count);
    }

    public List<Review> getAll() {
        return template.query("SELECT " +
                        "r.id AS id, " +
                        "r.content AS content, " +
                        "r.is_positive AS is_positive, " +
                        "r.user_id AS user_id, " +
                        "r.film_id AS film_id, " +
                        "sum(rr.useful) AS useful " +
                        "FROM reviews AS r " +
                        "LEFT JOIN reviews_rates AS rr ON rr.review_id = r.id " +
                        "GROUP BY id",
                (rs, rowNum) -> Review.builder()
                        .reviewId(rs.getInt("id"))
                        .content(rs.getString("content"))
                        .isPositive(rs.getBoolean("is_positive"))
                        .filmId(rs.getInt("film_id"))
                        .userId(rs.getInt("user_id"))
                        .useful(rs.getInt("useful"))
                        .build());
    }

    public void addLikeReview(Integer id, Integer userId) {
        String sql = "insert into reviews_rates (review_id, user_id, useful) values (?, ?, ?)";
        template.update(sql, id, userId, 1); // referential integrity(?)
        Review review = this.getReviewById(id);
        review.setUseful(this.getReviewRate(review.getReviewId()));
        this.update(review);
    }

    public void deleteLikeReview(Integer id, Integer userId) {
        String sql = "delete from reviews_rates where review_id = ? and user_id = ? and useful = 1";
        template.update(sql, id, userId);
        Review review = this.getReviewById(id);
        this.update(review);
    }


    public void addDisLikeToReview(Integer id, Integer userId) {
        String sql = "insert into reviews_rates(review_id, user_id, useful) values (?, ?, ?)";
        template.update(sql, id, userId, -1);
        Review review = this.getReviewById(id);
        this.update(review);
    }

    public void deleteDisLikeFromReview(Integer id, Integer userId) {
        String sql = "delete from reviews_rates where review_id = ? and user_id = ? and useful = -1";
        template.update(sql, id, userId);
        Review review = this.getReviewById(id);
        this.update(review);
    }

    private int getReviewRate(int reviewId) {
        Integer count = 0;
        String sql = "select sum(useful) as result from reviews_rates where review_id = ?";
        SqlRowSet sqlRowSet = template.queryForRowSet(sql, reviewId);
        while (sqlRowSet.next()) {
            count = sqlRowSet.getInt("result");
        }
        //    count += template.queryForObject(
        //          "select sum(useful) as result from reviews_rates where review_id = ?",
        //           Integer.class, reviewId);
        return count;
    }

    private void throwNotFoundExceptionForNonExistentId(int id, String tableName) {
        String select = "select exists (select id from " + tableName + " where id = ?) as match";
        if (Boolean.FALSE.equals(template.queryForObject(select,
                (rs, rowNum) -> rs.getBoolean("match"), id))) {
            throw new NotFoundException("No " + tableName + " with such ID: " + id);
        }
    }
}
