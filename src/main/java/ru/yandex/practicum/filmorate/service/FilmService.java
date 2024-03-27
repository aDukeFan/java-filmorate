package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film create(Film film);

    Film update(Film film);

    Film getById(int id);

    List<Film> findAll();

    public Film addLike(int filmId, int userId);

    public Film removeLike(int filmId, int userId);

    public List<Film> getTopPopularFilms(int count);
}
