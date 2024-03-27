package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.ecxeption.NotFoundException;
import ru.yandex.practicum.filmorate.ecxeption.RepeatException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
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
        if (filmStorage.getById(filmId) == null) {
            throw new NotFoundException("No film with such id");
        }
        if (userService.getById(userId) == null) {
            throw new NotFoundException("No user with such id");
        }
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
        if (filmStorage.getById(filmId) == null) {
            throw new NotFoundException("No film with such id");
        }
        if (userService.getById(userId) == null) {
            throw new NotFoundException("No user with such id");
        }
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
        return filmStorage.findAll().stream().filter(film -> !film.getLikes().isEmpty())
                .sorted(new Comparator<Film>() {
                    @Override
                    public int compare(Film o1, Film o2) {
                        if (o1.getLikes().size() > o2.getLikes().size()) {
                            return -1;
                        } else if (o1.getLikes().size() == o2.getLikes().size()) {
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                })
                .limit(count)
                .collect(Collectors.toList());
    }
}
