package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.RatingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/mpa")
public class RatingController {

    private RatingService service;

    @PostMapping()
    public Rating create(@Valid @RequestBody Rating rating) {
        return service.create(rating);
    }

    @PutMapping()
    public Rating update(@Valid @RequestBody Rating rating) {
        return service.update(rating);
    }

    @GetMapping("{ratingId}")
    public Rating getById(@PathVariable int ratingId) {
        return service.getById(ratingId);
    }

    @GetMapping()
    public List<Rating> getAll() {
        return service.getAll();
    }

    @DeleteMapping("{ratingId}")
    public void remove(@PathVariable int ratingId) {
        service.remove(ratingId);
    }
}
