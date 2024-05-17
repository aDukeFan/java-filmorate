package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {

    private FilmService filmService;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
    }

    @GetMapping("{id}")
    public Film getById(@PathVariable Integer id) {
        return filmService.getById(id);
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable int filmId,
                        @PathVariable int userId) {
        return filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable int filmId,
                           @PathVariable int userId) {
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping("popular") //Если значение параметра count не задано, верните первые 10.
    public List<Film> findTopPopularFilms(@RequestParam(required = false, defaultValue = "10") int count,
                                          @RequestParam(required = false, defaultValue = "0") int genreId,
                                          @RequestParam(required = false, defaultValue = "0") int year) {
        return filmService.getTopPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> findDirectorsFilms(@PathVariable Integer directorId, @RequestParam String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/search")
    public List<Film> searchByString(@RequestParam String query, @RequestParam String by) {
        return filmService.getFilmsByDirectorOrTitleByQuery(query, by);
    }

    @DeleteMapping("/{filmId}")
    public void delFilmById(@PathVariable Integer filmId) {
        filmService.delFilmById(filmId);
    }

    @GetMapping("common")
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}
