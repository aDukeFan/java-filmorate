package ru.yandex.practicum.filmorate.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.PackagePrivate;
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
public class ReviewRepository {
    JdbcTemplate jdbcTemplate;

    public ReviewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Review create(Review review) {
        String sql = "insert into reviews(content, is_positive, user_id, film_id) values(?,?,?,?)";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(),
                review.getUserId(), review.getFilmId());
        String sqlForGettingId = "select id from reviews order by id desc limit 1";
        Integer reviewId = jdbcTemplate.queryForObject(sqlForGettingId, Integer.class);
        review.setReviewId(reviewId);
        log.info("Создан отзыв с id {}", review.getReviewId());
        return review;
    }

    public Review update(Review review) {
        String sql = "update reviews set content = ?, is_positive = ?, user_id = ?, film_id = ? where id = ?";
        jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId(),
                review.getReviewId());
        log.info("Обновлен отзыв с id {}", review.getReviewId());
        return review;
    }

    public void delete(Integer id) {
        String sql = "delete from reviews where id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Удален отзыв с id {}", id);
    }

    public Review getReviewById(Integer id) {
        String sql = "select * from reviews where id = ?";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
        while (sqlRowSet.next()) {
            Review review = Review.builder().build();
            review.setReviewId(sqlRowSet.getInt("id"));
            review.setContent(sqlRowSet.getString("content"));
            review.setIsPositive(sqlRowSet.getBoolean("is_positive"));
            review.setUserId(sqlRowSet.getInt("user_id"));
            review.setFilmId(sqlRowSet.getInt("film_id"));
            review.setUseful(sqlRowSet.getInt("useful"));
            int likes = this.getLikesCount(review.getReviewId());
            int dislikes = this.getDisLikesCount(review.getReviewId());
            review.setUseful(likes - dislikes);
            return review;
        }
        return null;
    }

    public List<Review> getAllReviewsByFilmId(Integer filmId, Integer count) {
        if (count == null && filmId != null) {
            return this.getAllFilmsWithoutCount(filmId);
        }
        if (filmId == null && count != null) {
            return this.getAllFilmsWithoutIdFilm(count);
        }
        if (count != null && filmId != null) {
            return this.getAllReviewsWithParams(filmId, count);
        }
        return this.getAllReviewsWithoutParams();
    }

    private List<Review> getAllFilmsWithoutCount(Integer filmId) {
        List<Review> reviews = new ArrayList<>();
        String sql = "select * from reviews where film_id = ? order by useful limit (?)";
        reviews = jdbcTemplate.query(sql, (rs, rowNum) -> Review.builder()
                .reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .filmId(rs.getInt("film_id"))
                .userId(rs.getInt("user_id"))
                .useful(this.getLikesCount(rs.getInt("id")) - this.getDisLikesCount(rs.getInt("id")))
                .build(), filmId, 10);
        return reviews;
    }

    private List<Review> getAllFilmsWithoutIdFilm(int count) {
        List<Review> reviews = new ArrayList<>();
        String sql ="select * from reviews order by useful limit (?)";
        reviews = jdbcTemplate.query(sql, (rs, rowNum) -> Review.builder()
                .reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .filmId(rs.getInt("film_id"))
                .userId(rs.getInt("user_id"))
                .useful(this.getLikesCount(rs.getInt("id")) - this.getDisLikesCount(rs.getInt("id")))
                .build(), count);
        return reviews;
    }

    private List<Review> getAllReviewsWithParams(Integer filmId, Integer count) {
        List<Review> reviews = new ArrayList<>();
        String sql = "select * from reviews where film_id = ? order by useful limit (?)";
        reviews = jdbcTemplate.query(sql, (rs, rowNum) -> Review.builder()
                .reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .filmId(rs.getInt("film_id"))
                .userId(rs.getInt("user_id"))
                .useful(this.getLikesCount(rs.getInt("id")) - this.getDisLikesCount(rs.getInt("id")))
                .build(), filmId, count);
        return reviews;
    }

    public List<Review> getAllReviewsWithoutParams() {
        List<Review> reviews = new ArrayList<>();
        String sql = "select * from reviews order by useful desc";
        reviews = jdbcTemplate.query(sql, (rs, rowNum) -> Review.builder()
                .reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .filmId(rs.getInt("film_id"))
                .userId(rs.getInt("user_id"))
                .useful(this.getLikesCount(rs.getInt("id")) - this.getDisLikesCount(rs.getInt("id")))
                .build());
        return reviews;
    }

    public void addLikeReview(Integer id, Integer userId) {
        String sql = "insert into reviews_likes(review_id, user_id, review_like) values (?, ?, ?)";
        jdbcTemplate.update(sql, id, userId, 1);
        Review review = this.getReviewById(id);
        this.update(review);
    }

    public void deleteLikeReview(Integer id, Integer userId) {
        String sql = "delete from reviews_likes where review_id = ? and user_id = ? and review_like = 1";
        jdbcTemplate.update(sql, id, userId);
        Review review = this.getReviewById(id);
        this.update(review);
    }

    private int getLikesCount(int id) {
        int likes = 0;
        String sql = "select count(user_id) as likes from reviews_likes where review_id = ? and review_like = 1";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
        while (sqlRowSet.next()) {
            likes = sqlRowSet.getInt("likes");
        }
        return likes;
    }

    public void addDisLikeReview(Integer id, Integer userId) {
        String sql = "insert into reviews_likes(review_id, user_id, review_like) values (?, ?, ?)";
        jdbcTemplate.update(sql, id, userId, -1);
        Review review = this.getReviewById(id);
        this.update(review);
    }

    public void deleteDisLikeReview(Integer id, Integer userId) {
        String sql = "delete from reviews_likes where review_id = ? and user_id = ? and review_like = -1";
        jdbcTemplate.update(sql, id, userId);
        Review review = this.getReviewById(id);
        this.update(review);
    }

    private int getDisLikesCount(int id) {
        int dislikes = 0;
        String sql = "select count(user_id) as dislikes from reviews_likes where review_id = ? and review_like = -1";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(sql, id);
        while (sqlRowSet.next()) {
            dislikes = sqlRowSet.getInt("dislikes");
        }
        return dislikes;
    }
}
