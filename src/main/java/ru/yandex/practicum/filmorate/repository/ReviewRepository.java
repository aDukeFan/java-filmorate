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
        return review;
    }

    public Review update(Review review) {
        String sql = "update reviews set content = ?, is_positive = ?, user_id = ?, film_id = ? where id = ?";
        template.update(sql, review.getContent(), review.getIsPositive(), review.getUserId(), review.getFilmId(),
                review.getReviewId());
        log.info("Обновлен отзыв с id {}", review.getReviewId());
        return review;
    }

    public void delete(Integer id) {
        String sql = "delete from reviews where id = ?";
        template.update(sql, id);
        log.info("Удален отзыв с id {}", id);
    }

    public Review getReviewById(Integer id) {
        String sql = "select * from reviews where id = ?";
        SqlRowSet sqlRowSet = template.queryForRowSet(sql, id);
        while (sqlRowSet.next()) {
            Review review = Review.builder().build();
            review.setReviewId(sqlRowSet.getInt("id"));
            review.setContent(sqlRowSet.getString("content"));
            review.setIsPositive(sqlRowSet.getBoolean("is_positive"));
            review.setUserId(sqlRowSet.getInt("user_id"));
            review.setFilmId(sqlRowSet.getInt("film_id"));
            review.setUseful(sqlRowSet.getInt("useful"));
//            int likes = this.getLikesCount(review.getReviewId());
//            int dislikes = this.getDisLikesCount(review.getReviewId());
            review.setUseful(this.getReviewRate(review.getReviewId()));
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
        reviews = template.query(sql, (rs, rowNum) -> Review.builder()
                .reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .filmId(rs.getInt("film_id"))
                .userId(rs.getInt("user_id"))
                .useful(this.getReviewRate(rs.getInt("id")))
                //this.getLikesCount(rs.getInt("id")) - this.getDisLikesCount(rs.getInt("id")))
                .build(), filmId, 10);
        return reviews;
    }

    private List<Review> getAllFilmsWithoutIdFilm(int count) {
        List<Review> reviews = new ArrayList<>();
        String sql ="select * from reviews order by useful limit (?)";
        reviews = template.query(sql, (rs, rowNum) -> Review.builder()
                .reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .filmId(rs.getInt("film_id"))
                .userId(rs.getInt("user_id"))
                .useful(this.getReviewRate(rs.getInt("id")))
                //this.getLikesCount(rs.getInt("id")) - this.getDisLikesCount(rs.getInt("id")))
                .build(), count);
        return reviews;
    }

    private List<Review> getAllReviewsWithParams(Integer filmId, Integer count) {
        List<Review> reviews = new ArrayList<>();
        String sql = "select * from reviews where film_id = ? order by useful limit (?)";
        reviews = template.query(sql, (rs, rowNum) -> Review.builder()
                .reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .filmId(rs.getInt("film_id"))
                .userId(rs.getInt("user_id"))
                .useful(this.getReviewRate(rs.getInt("id")))
                // this.getLikesCount(rs.getInt("id")) - this.getDisLikesCount(rs.getInt("id")))
                .build(), filmId, count);
        return reviews;
    }

    public List<Review> getAllReviewsWithoutParams() {
        List<Review> reviews = new ArrayList<>();
        String sql = "select * from reviews order by useful desc";
        reviews = template.query(sql, (rs, rowNum) -> Review.builder()
                .reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .filmId(rs.getInt("film_id"))
                .userId(rs.getInt("user_id"))
                .useful(this.getReviewRate(rs.getInt("id")))
                .build());
        return reviews;
    }

    public void addLikeReview(Integer id, Integer userId) {
        String sql = "insert into reviews_likes(review_id, user_id, review_like) values (?, ?, ?)";
        template.update(sql, id, userId, 1);
        Review review = this.getReviewById(id);
        this.update(review);
    }

    public void deleteLikeReview(Integer id, Integer userId) {
        String sql = "delete from reviews_likes where review_id = ? and user_id = ? and review_like = 1";
        template.update(sql, id, userId);
        Review review = this.getReviewById(id);
        this.update(review);
    }


    public void addDisLikeToReview(Integer id, Integer userId) {
        String sql = "insert into reviews_likes(review_id, user_id, review_like) values (?, ?, ?)";
        template.update(sql, id, userId, -1);
        Review review = this.getReviewById(id);
        this.update(review);
    }

    public void deleteDisLikeFromReview(Integer id, Integer userId) {
        String sql = "delete from reviews_likes where review_id = ? and user_id = ? and review_like = -1";
        template.update(sql, id, userId);
        Review review = this.getReviewById(id);
        this.update(review);
    }

    private int getReviewRate(int reviewId) {
        Integer count = 0;
        count += template.queryForObject(
                "select sum(useful) as result from reviews_rate where review_id = ?",
                Integer.class, reviewId);
        return count;
    }

//    private int getLikesCount(int id) {
//        int likes = 0;
//        String sql = "select count(user_id) as likes from reviews_likes where review_id = ? and review_like = 1";
//        SqlRowSet sqlRowSet = template.queryForRowSet(sql, id);
//        while (sqlRowSet.next()) {
//            likes = sqlRowSet.getInt("likes");
//        }
//        return likes;
//    }

//    private int getDisLikesCount(int id) {
//        int dislikes = 0;
//        String sql = "select count(user_id) as dislikes from reviews_likes where review_id = ? and review_like = -1";
//        SqlRowSet sqlRowSet = template.queryForRowSet(sql, id);
//        while (sqlRowSet.next()) {
//            dislikes = sqlRowSet.getInt("dislikes");
//        }
//        return dislikes;
//    }

    private void throwNotFoundExceptionForNonExistentId(int id, String tableName) {
        String select = "select exists (select id from " + tableName + " where id = ?) as match";
        if (Boolean.FALSE.equals(template.queryForObject(select,
                (rs, rowNum) -> rs.getBoolean("match"), id))) {
            throw new NotFoundException("No " + tableName + " with such ID: " + id);
        }
    }
}
