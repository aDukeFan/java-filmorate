package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {

    public Review create(Review review);

    public Review update(Review review);

    public void delete(Integer id);

    public Review getReviewById(Integer id);

    public List<Review> getAll(Integer filmId, Integer count);

    public void addLikeReview(Integer id, Integer userId);

    public void deleteLikeReview(Integer id, Integer userId);

    public void addDisLikeReview(Integer id, Integer userId);

    public void deleteDisLikeReview(Integer id, Integer userId);

}
