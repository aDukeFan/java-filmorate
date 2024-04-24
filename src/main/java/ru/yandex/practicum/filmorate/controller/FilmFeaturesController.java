package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.repository.FilmFeaturesRepository;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping
public class FilmFeaturesController {
    private FilmFeaturesRepository repository;

    @PostMapping("/genres")
    public Genre createGenre(@Valid @RequestBody Genre genre) {
        return repository.createGenre(genre);
    }

    @PutMapping("/genres")
    public Genre updateGenre(@Valid @RequestBody Genre genre) {
        return repository.updateGenre(genre);
    }

    @GetMapping("/genres/{genreId}")
    public Genre getGenreById(@PathVariable int genreId) {
        return repository.getGenreById(genreId);
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return repository.getAllGenres();
    }

    @DeleteMapping("/genres/{genresId}")
    public void removeGenre(@PathVariable int genreId) {
        repository.removeGenre(genreId);
    }

    @PostMapping("/mpa")
    public Rating createRating(@Valid @RequestBody Rating rating) {
        return repository.createRating(rating);
    }

    @PutMapping("/mpa")
    public Rating updateRating(@Valid @RequestBody Rating rating) {
        return repository.updateRating(rating);
    }

    @GetMapping("/mpa/{ratingId}")
    public Rating getRatingById(@PathVariable int ratingId) {
        return repository.getRatingById(ratingId);
    }

    @GetMapping("/mpa")
    public List<Rating> getAllRatings() {
        return repository.getAllRatings();
    }

    @DeleteMapping("/mpa/{ratingId}")
    public void removeRating(@PathVariable int ratingId) {
        repository.removeRating(ratingId);
    }
}
