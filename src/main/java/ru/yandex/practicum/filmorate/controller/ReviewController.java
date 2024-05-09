package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review create(@RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable(required = true) Integer id) {
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable(required = true) Integer id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping()
    public List<Review> getAllReviewsByFilmId(@RequestParam Integer filmId, @RequestParam(required = false) Integer count) {
        return reviewService.getAllReviewsByFilmId(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLikeReview(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.addLikeReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeReview(Integer id, Integer userId) {
        reviewService.deleteLikeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDisLikeReview(Integer id, Integer userId) {
        reviewService.addDisLikeReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDisLikeReview(Integer id, Integer userId) {
        reviewService.deleteDisLikeReview(id, userId);
    }
}
