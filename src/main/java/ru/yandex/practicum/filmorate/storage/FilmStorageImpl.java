package ru.yandex.practicum.filmorate.storage;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.RepeatException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class FilmStorageImpl implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    private int generateId() {
        return nextId++;
    }

    @Override
    public Film create(Film film) {
        if (films.containsKey(film.getId())) {
            log.info("Film \"{}\" doesn't be saved, 'cause  is already exist\n", film.getName());
            throw new RepeatException("Film already exist");
        } else {
            int id = generateId();
            film.setId(id);
            films.put(id, film);
            log.info("Film \"{}\" is successfully saved\n", film.getName());
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.info("Film doesn't be updated, 'cause It has wrong ID\n");
            throw new NotFoundException("No film with such id");
        } else {
            films.replace(film.getId(), film);
            log.info("Film \"{}\" is successfully updated", film.getName());
        }
        return film;
    }

    @Override
    public Film getById(int id) {
        if (!films.containsKey(id)) {
            log.info("Film doesn't be updated, 'cause It has wrong ID\n");
            throw new NotFoundException("No film with such id");
        }
        return films.get(id);
    }

    @Override
    public List<Film> findAll() {
        log.info("Films are showed");
        return new ArrayList<>(films.values());
    }
}
