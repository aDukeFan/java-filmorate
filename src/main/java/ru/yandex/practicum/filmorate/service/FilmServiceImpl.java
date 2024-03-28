package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.ecxeption.RepeatException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;

    private final UserService userService;

    @Override
    public Film create(Film film) {
        filmStorage.create(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        filmStorage.update(film);
        return film;
    }

    @Override
    public Film getById(int id) {
        return filmStorage.getById(id);
    }

    @Override
    public List<Film> findAll() {
        log.info("Films are showed");
        return filmStorage.findAll();
    }

    @Override
    public Film addLike(int filmId, int userId) {
        userService.getById(userId);
        Film film = filmStorage.getById(filmId);
        if (film.getLikes().contains(userId)) {
            throw new RepeatException("Like was already set");
        }
        film.addLike(userId);
        update(film);
        return film;
    }

    @Override
    public Film removeLike(int filmId, int userId) {
        userService.getById(userId);
        Film film = filmStorage.getById(filmId);
        if (!film.getLikes().contains(userId)) {
            throw new RepeatException("No likes by user with same id");
        }
        film.removeLike(userId);
        filmStorage.update(film);
        return film;
    }

    @Override
    public List<Film> getTopPopularFilms(int count) {
        return filmStorage.findAll().stream()
                .filter(film -> !film.getLikes().isEmpty())
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
