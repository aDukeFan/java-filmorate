package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
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
    public List<Review> getAllReviewsByFilmId(@RequestParam(required = false) Integer filmId, @RequestParam(required = false) Integer count) {
        return reviewService.getAllReviewsByFilmId(filmId, count);
    }


    @PutMapping("/{id}/like/{userId}")
    public void addLikeReview(@PathVariable Integer id, @PathVariable Integer userId) {
        reviewService.addLikeReview(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLikeReview(@PathVariable Integer id,@PathVariable Integer userId) {
        reviewService.deleteLikeReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDisLikeReview(@PathVariable Integer id,@PathVariable Integer userId) {
        reviewService.addDisLikeReview(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDisLikeReview(@PathVariable Integer id,@PathVariable Integer userId) {
        reviewService.deleteDisLikeReview(id, userId);
    }
}
