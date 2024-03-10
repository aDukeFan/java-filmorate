package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.HashSet;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final HashSet<Film> films = new HashSet<>();
    private int nextId = 1;

    private int generateId() {
        return nextId++;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws Exception {
        if (films.contains(film)) {
            log.info("Film \"{}\" doesn't be saved, 'cause  is already exist\n", film.getName());
            throw new IllegalArgumentException("FilmAlreadyExistException");
        } else {
            int id = generateId();
            film.setId(id);
            films.add(film);
            log.info("Film \"{}\" is successfully saved\n", film.getName());
        }
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) throws Exception {
        if (!films.contains(film)) {
            log.info("Film doesn't be updated, 'cause It has wrong ID\n");
            throw new IllegalArgumentException("ThereIsNoSuchFilmException");
        } else {
            films.add(film);
            log.info("Film \"{}\" is successfully updated", film.getName());
        }
        return film;
    }

    @GetMapping
    public HashSet<Film> findAll() {
        log.info("Films are showed");
        return films;
    }
}
