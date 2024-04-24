package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {

    private FilmRepository filmRepository;

    @Override
    public Film create(Film film) {
        filmRepository.create(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        filmRepository.update(film);
        return film;
    }

    @Override
    public Film getById(int id) {
        return filmRepository.getById(id);
    }

    @Override
    public List<Film> findAll() {
        log.info("Films are showed");
        return filmRepository.findAll();
    }

    @Override
    public Film addLike(int filmId, int userId) {
        return filmRepository.addLike(filmId, userId);
    }

    @Override
    public Film removeLike(int filmId, int userId) {
        return filmRepository.removeLike(filmId, userId);
    }

    @Override
    public List<Film> getTopPopularFilms(int count) {
        return filmRepository.getTopPopularFilms(count);
    }
}
