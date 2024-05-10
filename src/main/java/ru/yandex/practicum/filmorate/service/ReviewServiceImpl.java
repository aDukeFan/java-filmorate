package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.repository.ReviewRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService{

    private ReviewRepository repository;

    @Override
    public Review create(Review review) {
        return repository.create(review);
    }

    @Override
    public Review update(Review review) {
        return repository.update(review);
    }

    @Override
    public void delete(Integer id) {
        repository.delete(id);
    }

    @Override
    public Review getReviewById(Integer id) {
        return repository.getReviewById(id);
    }

    @Override
    public List<Review> getAllReviewsByFilmId(Integer filmId, Integer count) {
        return repository.getAllReviewsByFilmId(filmId, count);
    }

    @Override
    public List<Review> getAllReviews() {
        return repository.getAllReviewsWithoutParams();
    }

    @Override
    public void addLikeReview(Integer id, Integer userId) {
        repository.addLikeReview(id, userId);
    }

    @Override
    public void deleteLikeReview(Integer id, Integer userId) {
        repository.deleteLikeReview(id, userId);
    }

    @Override
    public void addDisLikeReview(Integer id, Integer userId) {
        repository.addDisLikeToReview(id, userId);
    }

    @Override
    public void deleteDisLikeReview(Integer id, Integer userId) {
        repository.deleteDisLikeFromReview(id, userId);
    }
}
